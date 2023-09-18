package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitPasswordBinding
import com.example.bankingnow.base.BaseDialogFragment
import java.util.Locale

class RemitPasswordDialog : BaseDialogFragment<DialogRemitPasswordBinding>(R.layout.dialog_remit_password) {
    private val handler = Handler()


    private val ImageViewList : ArrayList<ImageView> = ArrayList()

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun initStartView() {
        ImageViewList.add(binding.ivPw6)
        ImageViewList.add(binding.ivPw5)
        ImageViewList.add(binding.ivPw4)
        ImageViewList.add(binding.ivPw3)
        ImageViewList.add(binding.ivPw2)
        ImageViewList.add(binding.ivPw1)


    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        // 비밀번호 맞으면 송금완료 화면 띄우기
//        RemitSuccessDialog().show(parentFragmentManager,"송금 완료")
//        this.dismiss()
    }


    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitPassword.setOnTouchListener { _, event ->
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
                        RemitCheckDialog().show(parentFragmentManager, "송금 체크")
                        dismiss()
                    } else if (distanceX > -10 && distanceX < 10) {
                        // 클릭으로 처리
                        RemitSuccessDialog().show(parentFragmentManager, "송금 성공")
                        dismiss()
                    }
                }
            }
            true
        }
    }

    private fun setFillCircle(index:Int){
        for (i in 1..index){
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fill_circle) }
            ImageViewList[i].setImageDrawable(drawable)
        }
    }
}