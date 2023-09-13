package com.example.bankingnow.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.databinding.FragmentBalanceBinding
import com.example.writenow.base.BaseFragment
import java.util.Locale

class BalanceFragment : BaseFragment<FragmentBalanceBinding>(R.layout.fragment_balance) {
    override fun initStartView() {
        super.initStartView()
    }

    override fun initDataBinding() {
        super.initDataBinding()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
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

}
