package com.example.musicvisualizer

import android.media.audiofx.Visualizer
import kotlin.math.abs
import kotlin.math.min

class VisualizerManager(
    private val audioSessionId: Int,
    private val onDataUpdate: (IntArray) -> Unit
) {
    private var visualizer: Visualizer? = null
    private val resolution = 64 // より細かい解像度
    
    fun start() {
        try {
            visualizer = Visualizer(audioSessionId).apply {
                enabled = false
                captureSize = Visualizer.getCaptureSizeRange()[1]
                
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer,
                            waveform: ByteArray,
                            samplingRate: Int
                        ) {
                            val processed = processWaveform(waveform)
                            onDataUpdate(processed)
                        }
                        
                        override fun onFftDataCapture(
                            visualizer: Visualizer,
                            fft: ByteArray,
                            samplingRate: Int
                        ) {
                            // FFTデータも利用可能
                        }
                    },
                    Visualizer.getMaxCaptureRate(),
                    true,  // waveform
                    false  // fft
                )
                
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun processWaveform(data: ByteArray): IntArray {
        val processed = IntArray(resolution)
        val captureSize = data.size
        val groupSize = captureSize / resolution
        
        for (i in 0 until resolution) {
            val startIndex = i * groupSize
            val endIndex = min((i + 1) * groupSize, data.size)
            
            var sum = 0.0
            for (j in startIndex until endIndex) {
                sum += abs(data[j].toInt())
            }
            processed[i] = (sum / groupSize).toInt()
        }
        
        return processed
    }
    
    fun stop() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (e: Exception) {
            // Ignore
        }
        visualizer = null
    }
}
