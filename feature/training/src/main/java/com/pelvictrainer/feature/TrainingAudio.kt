package com.pelvictrainer.feature

import android.content.Context
import android.speech.tts.TextToSpeech
import com.pelvictrainer.domain.model.TrainingPhase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingAudio @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var currentVolume: Float = 0.8f

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.forLanguageTag("ru-RU")
                isReady = true
            }
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
    }

    fun speakPhase(phase: TrainingPhase) {
        if (!isReady || currentVolume <= 0f) return

        val text = when (phase) {
            TrainingPhase.SQUEEZE -> "Сжимайте"
            TrainingPhase.HOLD -> "Держите"
            TrainingPhase.RELAX -> "Расслабьтесь"
            TrainingPhase.FINISHED -> "Отлично, тренировка завершена"
            else -> return
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "phase_$phase")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}