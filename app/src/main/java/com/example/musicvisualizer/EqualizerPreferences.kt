package com.example.musicvisualizer

import android.content.Context
import android.content.SharedPreferences

class EqualizerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "equalizer_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ENABLED = "equalizer_enabled"
        private const val KEY_PRESET = "equalizer_preset"
        private const val KEY_CUSTOM_GAINS = "equalizer_custom_gains"
    }
    
    /**
     * イコライザーの有効/無効状態を保存
     */
    fun saveEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
    
    /**
     * イコライザーの有効/無効状態を取得
     */
    fun getEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, true)
    }
    
    /**
     * 選択されているプリセットを保存
     */
    fun savePreset(preset: EqualizerManager.EqualizerPreset) {
        prefs.edit().putString(KEY_PRESET, preset.name).apply()
    }
    
    /**
     * 選択されているプリセットを取得
     */
    fun getPreset(): EqualizerManager.EqualizerPreset {
        val presetName = prefs.getString(KEY_PRESET, EqualizerManager.EqualizerPreset.NORMAL.name)
        return try {
            EqualizerManager.EqualizerPreset.valueOf(presetName ?: "NORMAL")
        } catch (e: Exception) {
            EqualizerManager.EqualizerPreset.NORMAL
        }
    }
    
    /**
     * カスタムゲイン設定を保存
     */
    fun saveCustomGains(gains: List<Int>) {
        val gainsString = gains.joinToString(",")
        prefs.edit().putString(KEY_CUSTOM_GAINS, gainsString).apply()
    }
    
    /**
     * カスタムゲイン設定を取得
     */
    fun getCustomGains(): List<Int> {
        val gainsString = prefs.getString(KEY_CUSTOM_GAINS, null)
        return if (gainsString != null) {
            try {
                gainsString.split(",").map { it.toInt() }
            } catch (e: Exception) {
                List(10) { 0 }
            }
        } else {
            List(10) { 0 }
        }
    }
    
    /**
     * すべての設定をクリア
     */
    fun clear() {
        prefs.edit().clear().apply()
    }
}
