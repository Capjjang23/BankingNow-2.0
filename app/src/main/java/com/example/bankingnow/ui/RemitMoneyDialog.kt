package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitMoneyBinding
import com.example.writenow.base.BaseDialogFragment
import java.util.Locale

class RemitMoneyDialog: BaseDialogFragment<DialogRemitMoneyBinding>(R.layout.dialog_remit_money) {
    private var lastTouchTime: Long = 0
    private val doubleClickDelay: Long = 500 // 더블 클릭 간격 설정 (0.5초)
    private lateinit var tts: TextToSpeech
    private val TTS_ID = "TTS"

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    override fun initAfterBinding() {
        super.initAfterBinding()

        setTTS()
        setTouchScreen()
//        binding.dialogRemitMoney.setOnClickListener {
//            RemitBankDialog().show(parentFragmentManager,"보내실 은행")
//            this.dismiss()
//        }
    }


    private fun setTouchScreen() {
        binding.dialogRemitMoney.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                val timeInterval = currentTime - lastTouchTime
                if (currentTime - lastTouchTime < doubleClickDelay) {
                    // 더블 클릭 처리: 뒤로 가기
                    setFragmentResult("Back", bundleOf("isSuccess" to false))
                    dismiss()
                } else{
                    RemitBankDialog().show(parentFragmentManager,"보내실 은행")
                    dismiss()
                }
                lastTouchTime = currentTime
            }
            true
        }
    }

    private fun setTTS() {
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
    }
}