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
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.bankingnow.databinding.DialogRemitAccountBinding
import com.example.writenow.base.BaseDialogFragment
import java.util.Locale

class RemitAccountDialog : BaseDialogFragment<DialogRemitAccountBinding>(R.layout.dialog_remit_account) {
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
//        binding.dialogRemitAccount.setOnClickListener {
//            RemitCheckDialog().show(parentFragmentManager,"송금 확인")
//            this.dismiss()
//        }
    }


    private fun setTouchScreen() {
        binding.dialogRemitAccount.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isSingleClick) {
                        // 더블 클릭 처리: 뒤로 가기
                        RemitBankDialog().show(parentFragmentManager,"송금 확인")
                        dismiss()
                    } else {
                        // 첫 번째 클릭 시작
                        isSingleClick = true
                        handler.postDelayed({
                            if (isSingleClick) {
                                // 한 번 클릭 처리: Log 출력
                                RemitCheckDialog().show(parentFragmentManager,"송금")
                                dismiss()
                            }
                            isSingleClick = false
                        }, doubleClickDelay)
                    }
                }
            }
            true
        }
    }
}