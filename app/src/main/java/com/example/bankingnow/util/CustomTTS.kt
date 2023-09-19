package com.example.bankingnow.util

import android.content.Context
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class CustomTTS(mContext: Context) {
    lateinit var tts: TextToSpeech
    private val mContext = mContext

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
        initTTS()
    }

    fun initTTS(str:String = "") {
        tts = TextToSpeech(mContext, TextToSpeech.OnInitListener { status ->
            if (status!=TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
                tts.setPitch(1.1f)
                tts.setSpeechRate(1.0f)

                if (str.isNotBlank())
                    speak(str)

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

    /*
    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            // 말하기가 시작될 때 실행할 코드
        }

        override fun onDone(utteranceId: String?) {
            // 말하기가 완료된 후 실행할 코드
            if (event.isSuccess) {
                tts.speak(event.result.predicted_number, TextToSpeech.QUEUE_FLUSH, null, "UtteranceID")
                recorder.startOneRecord(filePath, true)
            }
        }

        override fun onError(utteranceId: String?) {
            // 말하기 도중 오류가 발생했을 때 실행할 코드
        }
    })
     */
}