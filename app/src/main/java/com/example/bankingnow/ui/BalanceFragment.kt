package com.example.bankingnow.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.FragmentBalanceBinding
import com.example.writenow.base.BaseFragment
import java.text.NumberFormat
import java.util.Locale

class BalanceFragment : BaseFragment<FragmentBalanceBinding>(R.layout.fragment_balance),
    RecordApiManager.getMyBalance {
    private val TTS_ID = "TTS"

    private val apiManager = RecordApiManager()

    override fun initStartView() {
        super.initStartView()

        apiManager.listener = this
        apiManager.getBalance()
    }

    override fun initDataBinding() {
        super.initDataBinding()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

//        setTTS("생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해생일축하해")

    }

    private fun setTouchScreen() {
        binding.fragmentBalance.setOnTouchListener { _, motionEvent ->
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

    private fun setTTS(message: String) {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status!=TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
                tts.setPitch(1.0f)
                tts.setSpeechRate(1.0f)

                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
                Log.d("TTS INIT", "SUCCESS")
            }
            else{
                Log.d("TTS INIT", "FAIL")
            }
        })
    }

    override fun getBalance(balance: Long) {
        Log.d("잔액확인", balance.toString())
        binding.tvBalance.text = addCommasToNumber(balance) + " 원"

    }

    fun addCommasToNumber(number: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }

}
