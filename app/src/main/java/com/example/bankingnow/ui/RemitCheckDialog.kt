package com.example.bankingnow.ui

import android.os.Handler
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogRemitCheckBinding
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.model.RemitRequestModel
import com.example.bankingnow.viewmodel.MainViewModel
import com.example.bankingnow.viewmodel.RemitViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.properties.Delegates

class RemitCheckDialog() : BaseDialogFragment<DialogRemitCheckBinding>(R.layout.dialog_remit_check) {
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }
    private lateinit var name: String
    private lateinit var money: String


    private val mainViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MainViewModel::class.java]
    }

    override fun initStartView() {
        super.initStartView()

        name = prefs.getString("Dpnm", "홍길동")
        money = addCommasToNumber(viewModel.remitLiveData.value!!.money.toLong())
    }
    override fun initDataBinding() {
        super.initDataBinding()

        binding.tvRemitName.text = name
        binding.tvRemitBank.text = "농협은행"
        binding.tvRemitMoney.text = money
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
        setUtil(resources.getString(R.string.RemitCheck_receiver_check, name, money))
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

                        RemitMoneyDialog().show(parentFragmentManager, "송금 금액")
                        dismiss()
                    } else if (distanceX < -100) {
                        // 왼쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        RemitPasswordDialog().show(parentFragmentManager, "비밀번호 입력")
                        dismiss()
                    }
                }
            }
            true
        }
    }


    private fun addCommasToNumber(number: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }
}