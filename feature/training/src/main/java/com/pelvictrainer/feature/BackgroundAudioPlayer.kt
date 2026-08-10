package com.pelvictrainer.feature

import android.content.Context
import android.media.MediaPlayer
import com.pelvictrainer.domain.model.BackgroundSound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: BackgroundSound = BackgroundSound.NONE

    fun start(sound: BackgroundSound) {
        if (sound == BackgroundSound.NONE) {
            stop()
            return
        }

        val resourceId = getSoundResourceId(sound)
        if (resourceId == 0) {
            stop()
            return
        }

        if (currentSound == sound && mediaPlayer?.isPlaying == true) {
            return
        }

        stop()

        try {
            mediaPlayer = MediaPlayer.create(context, resourceId).apply {
                isLooping = true
                setVolume(0.3f, 0.3f)
                start()
            }
            currentSound = sound
        } catch (e: Exception) {
            stop()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentSound = BackgroundSound.NONE
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun setVolume(volume: Float) {
        val adjustedVolume = (volume * 0.4f).coerceIn(0f, 0.4f)
        mediaPlayer?.setVolume(adjustedVolume, adjustedVolume)
    }

    private fun getSoundResourceId(sound: BackgroundSound): Int {
        return when (sound) {
            BackgroundSound.NONE -> 0
            BackgroundSound.RAIN -> context.resources.getIdentifier("rain", "raw", context.packageName)
            BackgroundSound.FOREST -> context.resources.getIdentifier("forest", "raw", context.packageName)
            BackgroundSound.OCEAN -> context.resources.getIdentifier("ocean", "raw", context.packageName)
            BackgroundSound.BINAURAL -> context.resources.getIdentifier("binaural", "raw", context.packageName)
        }
    }
}