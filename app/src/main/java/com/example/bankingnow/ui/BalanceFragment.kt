package com.example.bankingnow.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import com.example.bankingnow.R
import com.example.bankingnow.databinding.FragmentBalanceBinding
import com.example.writenow.base.BaseFragment
import java.util.Locale

class BalanceFragment : BaseFragment<FragmentBalanceBinding>(R.layout.fragment_balance) {
    private var lastTouchTime: Long = 0
    private val doubleClickDelay: Long = 500 // 더블 클릭 간격 설정 (0.5초)
    private lateinit var tts: TextToSpeech
    private val TTS_ID = "TTS"

    override fun initStartView() {
        super.initStartView()
    }

    override fun initDataBinding() {
        super.initDataBinding()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
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
        binding.btnSpeak.setOnClickListener {
            tts?.speak("생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해", TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
    }

    private fun setTouchScreen() {
        binding.view.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTouchTime < doubleClickDelay) {
                    // 더블 클릭 처리: 뒤로 가기
                    requireActivity().onBackPressed()
                }
                lastTouchTime = currentTime
            }
            true
        }
    }
}
