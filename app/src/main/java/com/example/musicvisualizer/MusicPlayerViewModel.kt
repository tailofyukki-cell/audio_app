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
    }
    
    fun loadMusic(uri: Uri, name: String) {
        stopProgressUpdates()
        visualizerManager?.stop()
        
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
        
        // Visualizerを初期化
        val sessionId = musicPlayer?.getAudioSessionId() ?: 0
        visualizerManager = VisualizerManager(
            audioSessionId = sessionId,
            onDataUpdate = { data ->
                viewModelScope.launch {
                    visualizerData.value = data
                }
            }
        )
    }
    
    private fun handleCompletion() {
        isPlaying.value = false
        stopProgressUpdates()
        visualizerManager?.stop()
        currentPosition.value = 0
    }
    
    fun play() {
        if (!isPrepared.value) return
        
        musicPlayer?.play()
        visualizerManager?.start()
        isPlaying.value = true
        startProgressUpdates()
    }
    
    fun pause() {
        musicPlayer?.pause()
        visualizerManager?.stop()
        isPlaying.value = false
        stopProgressUpdates()
    }
    
    fun stop() {
        musicPlayer?.stop()
        visualizerManager?.stop()
        isPlaying.value = false
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
    
    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (isActive && isPlaying.value) {
                currentPosition.value = musicPlayer?.getCurrentPosition() ?: 0
                delay(100) // 100msごとに更新
            }
        }
    }
    
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }
    
    fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        visualizerManager?.stop()
        musicPlayer?.release()
    }
}
