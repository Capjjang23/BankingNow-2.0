package com.example.bankingnow.util

import android.content.Context
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class CustomTTS(mContext: Context) {
    lateinit var tts: TextToSpeech

    companion object { // singleton object
        var instance: CustomTTS? = null
        val TTS_ID = "TTS"
        fun getInstance(context: Context): CustomTTS {
            if (instance == null) {
                @Synchronized
                if (instance == null)
                    instance = CustomTTS(context)
            }
            return instance as CustomTTS
        }
    }

    init { // constructor
        tts = TextToSpeech(mContext, TextToSpeech.OnInitListener { status ->
            if (status!=TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
                tts.setPitch(1.0f)
                tts.setSpeechRate(1.0f)

                Log.d("TTS INIT", "SUCCESS")
            }
            else{
                Log.d("TTS INIT", "FAIL")
            }
        })
    }

    fun speak(msg: String) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
    }
    fun getTTS(): String {
        return TTS_ID
    }
}