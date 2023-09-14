package com.example.bankingnow.ui

import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.databinding.DialogRemitMoneyBinding
import com.example.bankingnow.event.PostNumberEvent
import com.example.bankingnow.base.BaseDialogFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date


class RemitMoneyDialog: BaseDialogFragment<DialogRemitMoneyBinding>(R.layout.dialog_remit_money) {
    private val handler = Handler()

    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()

    private val stateList: Array<String> = arrayOf("RECORD_START", "RECORD_STOP", "SUCCESS")
    private val RECORD_START = 0
    private val RECORD_STOP = 1
    private val SUCCESS = 2
    private var state: String = stateList[RECORD_START]

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
            customTTS.speak("12342424324324432")
        }
    }


    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitMoney.setOnTouchListener { _, event ->
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
                        recorder.stopRecording()
                        setFragmentResult("Back", bundleOf("isSuccess" to false))
                        dismiss()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        Log.d("통신?","클릭")
                        recorder.startOneRecord(filePath, true)
                    }
                }
            }
            true // 이벤트 소비
        }
    }
}