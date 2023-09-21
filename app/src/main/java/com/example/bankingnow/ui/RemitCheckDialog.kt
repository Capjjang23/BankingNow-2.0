package com.example.bankingnow.ui

import android.os.Handler
import android.view.MotionEvent
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogRemitCheckBinding
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.model.RemitCheckModel
import com.example.bankingnow.model.RemitRequestModel

class RemitCheckDialog(remitInfo: RemitCheckModel) : BaseDialogFragment<DialogRemitCheckBinding>(R.layout.dialog_remit_check) {
    private val handler = Handler()
    private val remitInfo: RemitCheckModel = remitInfo
    private var recordApiManager = RecordApiManager()

    override fun initDataBinding() {
        super.initDataBinding()

        binding.tvRemitName.text = remitInfo.name
        binding.tvRemitBank.text = remitInfo.user.bank
        binding.tvRemitMoney.text = remitInfo.money
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
        setUtil(resources.getString(R.string.RemitCheck_receiver_check, remitInfo.name, remitInfo.money))

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
                    if (distanceX > 100) {
                        // 오른쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        RemitAccountDialog().show(parentFragmentManager, "비밀 번호")
                        dismiss()
                    } else if (distanceX < -100) {
                        // 왼쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        RemitPasswordDialog(remitInfo).show(parentFragmentManager, "송금")
                        dismiss()
                    }
                }
            }
            true
        }
    }
}