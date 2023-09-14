package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.bankingnow.databinding.DialogRemitBankBinding
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.event.PostNumberEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date
import java.util.Locale

class RemitBankDialog : BaseDialogFragment<DialogRemitBankBinding>(R.layout.dialog_remit_bank) {
    private val handler = Handler()

    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()

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

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
                // 말하기가 완료된 후 실행할 코드
                // tts 이벤트는 UI 쓰레드에서 호출해야 함
                Handler(Looper.getMainLooper()).post {
                    recorder.startOneRecord(filePath, true)
                }
            }

            override fun onError(utteranceId: String?) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // EventBus 등록
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        // EventBus 해제
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNumberEvent(event: PostNumberEvent) {
        if (event.isSuccess){
            customTTS.speak("1")
        }
    }


    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitBank.setOnTouchListener { _, event ->
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
                        setFragmentResult("Back", bundleOf("isSuccess" to false))
                        dismiss()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        recorder.startOneRecord(filePath, true)
                    }
                }
            }
            true // 이벤트 소비
        }
    }
}