package com.example.bankingnow.ui

import android.annotation.SuppressLint
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitMoneyBinding
import com.example.bankingnow.event.PostNumberEvent
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.util.Recorder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date


class RemitMoneyDialog: BaseDialogFragment<DialogRemitMoneyBinding>(R.layout.dialog_remit_money) {
    private val handler = Handler()

    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()

    private val stateList: Array<String> = arrayOf("FAIL", "RECORD_START", "SUCCESS")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val result: MutableLiveData<String> = MutableLiveData()

    override fun initDataBinding() {
        super.initDataBinding()

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }

        result.observe(viewLifecycleOwner) {
            binding.tvMoney.text = it
        }
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
            customTTS.speak("12342424324324432")
            result.postValue(result.value + event.result.predicted_number)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
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
                    } else if (state=="SUCCESS" && distanceX > 100){
                        RemitAccountDialog().show(parentFragmentManager, "송금 계좌")
                        dismiss()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        when (state) {
                            "FAIL" -> {
                                idx.postValue(1)
                                recorder.startOneRecord(filePath, true)
                            }
                            "RECORD_START" -> {
                                idx.postValue(2)
                                recorder.stopRecording()
                                customTTS.speak("1000원. 다시 입력하시려면 터치, 다음 단계로 가시려면 오른쪽으로 스와이프 해주세요.")
                            }
                            "SUCCESS" -> {
                                idx.postValue(1)
                                recorder.startOneRecord(filePath, true)
                            }
                        }
                    }
                }
            }
            true // 이벤트 소비
        }
    }
}