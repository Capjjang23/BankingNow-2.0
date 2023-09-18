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
import androidx.lifecycle.MutableLiveData
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

    private val stateList: Array<String> = arrayOf("FAIL", "RECORD_START", "SUCCESS")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    override fun initDataBinding() {
        super.initDataBinding()

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
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
                        RemitMoneyDialog().show(parentFragmentManager, "송금 금액")
                        dismiss()
                    } else if (state=="SUCCESS" && distanceX > 100){
                        // 오른쪽으로 스와이프
                        RemitAccountDialog().show(parentFragmentManager, "송금 계좌")
                        dismiss()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        when (state) {
                            "FAIL" -> {
                                idx.postValue(1)
                                // stt 구현
                            }
                            "RECORD_START" -> {
                                idx.postValue(2)
                                customTTS.speak("은행 확인")
                            }
                            "SUCCESS" -> {
                                idx.postValue(1)
                                // stt 구현
                            }
                        }
                    }
                }
            }
            true // 이벤트 소비
        }
    }
}