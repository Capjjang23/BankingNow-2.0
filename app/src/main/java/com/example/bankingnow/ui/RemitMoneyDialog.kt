package com.example.bankingnow.ui

import android.annotation.SuppressLint
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.MotionEvent
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitMoneyBinding
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.util.CustomTTS
import com.example.bankingnow.util.CustomVibrator
import com.example.bankingnow.util.Recorder
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date


class RemitMoneyDialog: BaseDialogFragment<DialogRemitMoneyBinding>(R.layout.dialog_remit_money) {
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }

    private val handler = Handler()

    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()

    private val stateList: Array<String> = arrayOf("FAIL", "RECORD_START", "SUCCESS")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    private val result: MutableLiveData<String> = MutableLiveData("")

    override fun initAfterBinding() {
        super.initAfterBinding()

        setUtil(resources.getString(R.string.RemitMoney_info))
        setTouchScreen()

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }

        result.observe(viewLifecycleOwner) {
            binding.tvMoney.text = it
            viewModel.setRemitMoney(it)
        }
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
    fun onNumberEvent(event: NumberPublicEvent) {
        if (event.isSuccess){
            isResponse.postValue(true)

            if (idx.value == 1) {
                result.postValue(result.value + event.result.predicted_number)
                customTTS.speak(event.result.predicted_number)
                recorder.startOneRecord(filePath, true)
            } else {
                isResponse.postValue(false)
            }
        } else{
            isResponse.postValue(false)
            customTTS.speak(resources.getString(R.string.no_network))
            idx.postValue(0)
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
                    if (distanceX > 100) {
                        // 오른쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        recorder.stopRecording()
                        setFragmentResult("Back", bundleOf("isSuccess" to false))
                        dismiss()
                    } else if (state=="SUCCESS" && distanceX < -100){
                        // 왼쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }
                        Log.d("RemitIsNotFill",viewModel.toString())
                        RemitBankDialog().show(parentFragmentManager,"송금 계좌")
                        dismiss()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        when (state) {
                            "FAIL" -> {
                                idx.postValue(1)
                                result.value = ""
                                recorder.startOneRecord(filePath, true)
                            }
                            "RECORD_START" -> {
                                idx.postValue(2)
                                recorder.stopRecording()

                                val intResult = result.value!!.toInt()

                                val formattedString = getString(R.string.RemitMoney_money_check, intResult.toString())
                                customTTS.speak(formattedString)
                            }
                            "SUCCESS" -> {
                                idx.postValue(1)
                                result.value = ""
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
