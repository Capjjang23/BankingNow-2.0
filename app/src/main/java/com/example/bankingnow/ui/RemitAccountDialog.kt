package com.example.bankingnow.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitAccountBinding
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Thread.sleep
import java.util.Date

class RemitAccountDialog : BaseDialogFragment<DialogRemitAccountBinding>(R.layout.dialog_remit_account) {
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }

    private val stateList: Array<String> = arrayOf("FAIL", "RECORD_START", "SUCCESS")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    private val result: MutableLiveData<String> = MutableLiveData("")

    override fun initDataBinding() {
        super.initDataBinding()

        setUtil(resources.getString(R.string.RemitAccount_info))
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }

        result.observe(viewLifecycleOwner) {
            binding.tvAccount.text = it
            viewModel.setRemitAccount(it)
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
            } else {
                isResponse.postValue(false)
            }
        } else{
            isResponse.postValue(false)
            customTTS.speak(resources.getString(R.string.no_network))
            idx.postValue(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitAccount.setOnTouchListener { _, event ->
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

                        RemitMoneyDialog().show(parentFragmentManager,"송금 금액")
                        dismiss()
                    } else if (state=="SUCCESS" && distanceX < -100){
                        // 왼쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        val remitResultIsFill = viewModel.isFill()
                        if (remitResultIsFill) {
                            setFragmentResult("Check", bundleOf("isFill" to true))
                            dismiss()
                        }
                        else {
                            setFragmentResult("Check", bundleOf("isFill" to false))
                            dismiss()
                        }
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        when (state) {
                            "FAIL" -> {
                                result.value = ""
                                idx.postValue(1)

                                // 테스트
//                                idx.postValue(1)
//                                result.postValue("7848539105")
                            }
                            "RECORD_START" -> {
                                idx.postValue(2)
                                customTTS.speak(resources.getString(R.string.RemitAccount_check_account))
                            }
                            "SUCCESS" -> {
                                result.value = ""
                                idx.postValue(1)
                            }
                        }
                    }
                }
            }
            true
        }
    }
}