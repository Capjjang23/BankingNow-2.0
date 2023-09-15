package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitCheckBinding
import com.example.bankingnow.databinding.DialogRemitPasswordBinding
import com.example.bankingnow.base.BaseDialogFragment
import java.util.Locale

class RemitCheckDialog : BaseDialogFragment<DialogRemitCheckBinding>(R.layout.dialog_remit_check) {
    private val handler = Handler()

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

//        binding.dialogRemitCheck.setOnClickListener {
//            RemitPasswordDialog().show(parentFragmentManager,"비밀 번호")
//            this.dismiss()
//        }
    }


    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitCheck.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val distanceX = endX - startX

                    // 스와이프를 감지하기 위한 조건 설정
                    if (distanceX < -100) {
                        // 왼쪽으로 스와이프
                        RemitAccountDialog().show(parentFragmentManager, "비밀 번호")
                        dismiss()
                    } else if (distanceX > 100) {
                        // 오른쪽으로 스와이프
                        RemitPasswordDialog().show(parentFragmentManager, "송금")
                        dismiss()
                    }
                }
            }
            true
        }
    }
}