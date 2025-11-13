package com.example.musicvisualizer

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var musicPlayer: MusicPlayer? = null
    private var visualizerManager: VisualizerManager? = null
    private var equalizerManager: EqualizerManager? = null
    private var equalizerPreferences: EqualizerPreferences? = null
    private var progressUpdateJob: Job? = null
    
    val isPlaying = mutableStateOf(false)
    val visualizerData = mutableStateOf(IntArray(64) { 0 })
    val visualizerMode = mutableStateOf(VisualizerMode.BAR)
    val currentPosition = mutableStateOf(0)
    val duration = mutableStateOf(0)
    val volume = mutableStateOf(1.0f)
    val selectedMusicUri = mutableStateOf<Uri?>(null)
    val selectedMusicName = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>(null)
    val isPrepared = mutableStateOf(false)
    
    // イコライザー関連の状態
    val equalizerEnabled = mutableStateOf(true)
    val currentPreset = mutableStateOf(EqualizerManager.EqualizerPreset.NORMAL)
    val bandGains = mutableStateOf(List(10) { 0 })
    val equalizerExpanded = mutableStateOf(false)
    
    enum class VisualizerMode {
        BAR, WAVEFORM, CIRCULAR, SPECTRUM
    }
    
    fun initialize() {
        musicPlayer = MusicPlayer(getApplication()).apply {
            onCompletionListener = {
                handleCompletion()
            }
            onPreparedListener = {
                handlePrepared()
            }
            onErrorListener = { error ->
                errorMessage.value = error
            }
        }
        
        // イコライザー設定を初期化
        equalizerPreferences = EqualizerPreferences(getApplication())
        loadEqualizerSettings()
    }
    
    fun loadMusic(uri: Uri, name: String) {
        stopProgressUpdates()
        visualizerManager?.stop()
        equalizerManager?.release()
        
        selectedMusicUri.value = uri
        selectedMusicName.value = name
        isPrepared.value = false
        isPlaying.value = false
        currentPosition.value = 0
        duration.value = 0
        
        musicPlayer?.loadMusic(uri)
    }
    
    private fun handlePrepared() {
        isPrepared.value = true
        duration.value = musicPlayer?.getDuration() ?: 0
        
        val sessionId = musicPlayer?.getAudioSessionId() ?: 0
        
        // Visualizerを初期化
        visualizerManager = VisualizerManager(
            audioSessionId = sessionId,
            onDataUpdate = { data ->
                viewModelScope.launch {
                    visualizerData.value = data
                }
            }
        )
        
        // Equalizerを初期化
        equalizerManager = EqualizerManager(sessionId).apply {
            setEnabled(equalizerEnabled.value)
            
            // 保存されている設定を適用
            if (currentPreset.value == EqualizerManager.EqualizerPreset.CUSTOM) {
                setAllBandGains(bandGains.value)
            } else {
                applyPreset(currentPreset.value)
            }
        }
        
        // 現在のゲインを更新
        updateBandGains()
    }
    
    private fun handleCompletion() {
        isPlaying.value = false
        stopProgressUpdates()
        visualizerManager?.stop()
    }
    
    fun play() {
        if (isPrepared.value) {
            musicPlayer?.play()
            isPlaying.value = true
            visualizerManager?.start()
            startProgressUpdates()
        }
    }
    
    fun pause() {
        musicPlayer?.pause()
        isPlaying.value = false
        visualizerManager?.stop()
        stopProgressUpdates()
    }
    
    fun stop() {
        musicPlayer?.stop()
        isPlaying.value = false
        visualizerManager?.stop()
        stopProgressUpdates()
        currentPosition.value = 0
    }
    
    fun seekTo(position: Int) {
        musicPlayer?.seekTo(position)
        currentPosition.value = position
    }
    
    fun setVolume(newVolume: Float) {
        volume.value = newVolume
        musicPlayer?.setVolume(newVolume)
    }
    
    fun setVisualizerMode(mode: VisualizerMode) {
        visualizerMode.value = mode
    }
    
    // イコライザー関連のメソッド
    
    fun setEqualizerEnabled(enabled: Boolean) {
        equalizerEnabled.value = enabled
        equalizerManager?.setEnabled(enabled)
        equalizerPreferences?.saveEnabled(enabled)
    }
    
    fun setEqualizerPreset(preset: EqualizerManager.EqualizerPreset) {
        currentPreset.value = preset
        equalizerManager?.applyPreset(preset)
        updateBandGains()
        equalizerPreferences?.savePreset(preset)
        
        // Customプリセットの場合は、現在のゲインを保存
        if (preset == EqualizerManager.EqualizerPreset.CUSTOM) {
            equalizerPreferences?.saveCustomGains(bandGains.value)
        }
    }
    
    fun setBandGain(band: Int, gain: Int) {
        equalizerManager?.setBandGain(band, gain)
        
        // 状態を更新
        val newGains = bandGains.value.toMutableList()
        newGains[band] = gain
        bandGains.value = newGains
        
        // プリセットをCustomに変更
        if (currentPreset.value != EqualizerManager.EqualizerPreset.CUSTOM) {
            currentPreset.value = EqualizerManager.EqualizerPreset.CUSTOM
            equalizerPreferences?.savePreset(EqualizerManager.EqualizerPreset.CUSTOM)
        }
        
        // カスタムゲインを保存
        equalizerPreferences?.saveCustomGains(bandGains.value)
    }
    
    fun setEqualizerExpanded(expanded: Boolean) {
        equalizerExpanded.value = expanded
    }
    
    private fun updateBandGains() {
        val gains = equalizerManager?.getAllBandGains() ?: List(10) { 0 }
        bandGains.value = gains
    }
    
    private fun loadEqualizerSettings() {
        val prefs = equalizerPreferences ?: return
        
        equalizerEnabled.value = prefs.getEnabled()
        currentPreset.value = prefs.getPreset()
        
        if (currentPreset.value == EqualizerManager.EqualizerPreset.CUSTOM) {
            bandGains.value = prefs.getCustomGains()
        }
    }
    
    fun getEqualizerGainRange(): Pair<Int, Int> {
        return equalizerManager?.getGainRange() ?: Pair(-15, 15)
    }
    
    fun getBandFrequencies(): List<Int> {
        return equalizerManager?.bandFrequencies ?: listOf(
            31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000
        )
    }
    
    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isActive && isPlaying.value) {
                currentPosition.value = musicPlayer?.getCurrentPosition() ?: 0
                delay(100)
            }
        }
    }
    
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        visualizerManager?.stop()
        visualizerManager = null
        equalizerManager?.release()
        equalizerManager = null
        musicPlayer?.release()
        musicPlayer = null
    }
}
