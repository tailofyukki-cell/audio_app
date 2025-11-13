package com.example.musicvisualizer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.IOException

class MusicPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: Uri? = null
    
    var onCompletionListener: (() -> Unit)? = null
    var onPreparedListener: (() -> Unit)? = null
    var onErrorListener: ((String) -> Unit)? = null
    
    fun loadMusic(uri: Uri) {
        try {
            release()
            currentUri = uri
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                setOnCompletionListener {
                    onCompletionListener?.invoke()
                }
                setOnPreparedListener {
                    onPreparedListener?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    onErrorListener?.invoke("Error: what=$what, extra=$extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: IOException) {
            onErrorListener?.invoke("Failed to load music: ${e.message}")
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Illegal state: ${e.message}")
        }
    }
    
    fun play() {
        try {
            mediaPlayer?.start()
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Cannot play: ${e.message}")
        }
    }
    
    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Cannot pause: ${e.message}")
        }
    }
    
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Cannot stop: ${e.message}")
        }
    }
    
    fun seekTo(position: Int) {
        try {
            mediaPlayer?.seekTo(position)
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Cannot seek: ${e.message}")
        }
    }
    
    fun setVolume(volume: Float) {
        try {
            mediaPlayer?.setVolume(volume, volume)
        } catch (e: IllegalStateException) {
            onErrorListener?.invoke("Cannot set volume: ${e.message}")
        }
    }
    
    fun getAudioSessionId(): Int {
        return mediaPlayer?.audioSessionId ?: 0
    }
    
    fun getCurrentPosition(): Int {
        return try {
            mediaPlayer?.currentPosition ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }
    
    fun getDuration(): Int {
        return try {
            mediaPlayer?.duration ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }
    
    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (e: IllegalStateException) {
            false
        }
    }
    
    fun release() {
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaPlayer = null
    }
}
