package com.pelvictrainer.feature


import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale



class TrainingAudio(
    context: Context
) {


    private var tts: TextToSpeech? = null


    init {

        tts =
            TextToSpeech(context) { status ->

                if(status == TextToSpeech.SUCCESS) {

                    tts?.language = Locale("ru", "RU")

                }

            }

    }



    fun speak(
        text: String
    ) {

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "training_voice"
        )

    }



    fun release() {

        tts?.stop()
        tts?.shutdown()

    }


}