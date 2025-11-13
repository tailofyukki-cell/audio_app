package com.example.musicvisualizer

import android.media.audiofx.Equalizer
import android.util.Log

class EqualizerManager(audioSessionId: Int) {
    private var equalizer: Equalizer? = null
    private val TAG = "EqualizerManager"
    
    // 10バンドの中心周波数（Hz）- UI表示用
    val bandFrequencies = listOf(
        31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000
    )
    
    // 実際のバンド数
    private var actualBandCount: Int = 0
    
    // 10バンドの仮想ゲイン値（UI用）
    private val virtualBandGains = MutableList(10) { 0 }
    
    // プリセット定義（dB単位）
    enum class EqualizerPreset(val displayName: String, val gains: List<Int>) {
        NORMAL("Normal", listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),
        POP("Pop", listOf(-2, -1, 0, 2, 4, 4, 2, 0, -1, -2)),
        ROCK("Rock", listOf(5, 3, -3, -5, -2, 2, 5, 6, 6, 6)),
        JAZZ("Jazz", listOf(4, 3, 1, 2, -2, -2, 0, 2, 3, 4)),
        CLASSICAL("Classical", listOf(5, 4, 3, 2, -2, -2, 0, 2, 3, 4)),
        DANCE("Dance", listOf(6, 4, 2, 0, 0, -3, -4, -4, 0, 0)),
        CUSTOM("Custom", listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
    
    init {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            actualBandCount = getNumberOfBands().toInt()
            Log.d(TAG, "Equalizer initialized with $actualBandCount actual bands")
            
            // 実際のバンド周波数を確認
            for (i in 0 until actualBandCount) {
                val freq = equalizer?.getCenterFreq(i.toShort())
                Log.d(TAG, "Actual band $i: ${freq?.div(1000)}Hz")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize equalizer", e)
        }
    }
    
    /**
     * バンド数を取得
     */
    fun getNumberOfBands(): Short {
        return equalizer?.numberOfBands ?: 0
    }
    
    /**
     * 10バンド仮想インデックスを実際のバンドインデックスにマッピング
     */
    private fun mapVirtualToActualBand(virtualBand: Int): Short {
        if (actualBandCount == 0) return 0
        
        // 10バンドを実際のバンド数にマッピング
        // 例: 5バンドの場合、10バンドを2つずつグループ化
        val actualBand = (virtualBand * actualBandCount) / 10
        return actualBand.coerceIn(0, actualBandCount - 1).toShort()
    }
    
    /**
     * 特定のバンドのゲインを設定（10バンド仮想インデックス）
     * @param virtualBand 仮想バンド番号（0-9）
     * @param gainDb ゲイン（dB単位、-15～15）
     */
    fun setBandGain(virtualBand: Int, gainDb: Int) {
        try {
            val eq = equalizer ?: return
            
            // 仮想ゲイン値を保存
            if (virtualBand in 0..9) {
                virtualBandGains[virtualBand] = gainDb
            }
            
            // 実際のバンドにマッピングして設定
            val actualBand = mapVirtualToActualBand(virtualBand)
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]
            
            // dBをミリベル（mB）に変換（1dB = 100mB）
            val gainMb = (gainDb * 100).toShort()
            val clampedGain = gainMb.coerceIn(minLevel, maxLevel)
            
            eq.setBandLevel(actualBand, clampedGain)
            Log.d(TAG, "Set virtual band $virtualBand (actual $actualBand) to ${gainDb}dB")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set band gain", e)
        }
    }
    
    /**
     * 特定のバンドのゲインを取得（10バンド仮想インデックス）
     * @param virtualBand 仮想バンド番号（0-9）
     * @return ゲイン（dB単位）
     */
    fun getBandGain(virtualBand: Int): Int {
        // 仮想ゲイン値を返す
        return if (virtualBand in 0..9) {
            virtualBandGains[virtualBand]
        } else {
            0
        }
    }
    
    /**
     * プリセットを適用
     */
    fun applyPreset(preset: EqualizerPreset) {
        preset.gains.forEachIndexed { index, gain ->
            setBandGain(index, gain)
        }
        Log.d(TAG, "Applied preset: ${preset.displayName}")
    }
    
    /**
     * すべてのバンドのゲインを取得（10バンド）
     */
    fun getAllBandGains(): List<Int> {
        return virtualBandGains.toList()
    }
    
    /**
     * すべてのバンドのゲインを設定（10バンド）
     */
    fun setAllBandGains(gains: List<Int>) {
        gains.take(10).forEachIndexed { index, gain ->
            setBandGain(index, gain)
        }
    }
    
    /**
     * イコライザーの有効/無効を切り替え
     */
    fun setEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            Log.d(TAG, "Equalizer ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set equalizer enabled state", e)
        }
    }
    
    /**
     * イコライザーが有効かどうか
     */
    fun isEnabled(): Boolean {
        return equalizer?.enabled ?: false
    }
    
    /**
     * ゲインの範囲を取得（dB単位）
     */
    fun getGainRange(): Pair<Int, Int> {
        return try {
            val eq = equalizer ?: return Pair(-15, 15)
            val minDb = eq.bandLevelRange[0] / 100
            val maxDb = eq.bandLevelRange[1] / 100
            Pair(minDb, maxDb)
        } catch (e: Exception) {
            Pair(-15, 15)
        }
    }
    
    /**
     * リソースを解放
     */
    fun release() {
        try {
            equalizer?.release()
            equalizer = null
            Log.d(TAG, "Equalizer released")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release equalizer", e)
        }
    }
}
