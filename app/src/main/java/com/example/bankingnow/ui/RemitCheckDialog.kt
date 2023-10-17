package com.example.bankingnow.ui

import android.os.Handler
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogRemitCheckBinding
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.event.RemitEvent
import com.example.bankingnow.model.RemitRequestModel
import com.example.bankingnow.viewmodel.MainViewModel
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.properties.Delegates

class RemitCheckDialog() : BaseDialogFragment<DialogRemitCheckBinding>(R.layout.dialog_remit_check) {
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }
    private lateinit var name: String
    private lateinit var account: String
    private lateinit var money: String
    private lateinit var ttsMoney: String
    private lateinit var remitValue: RemitRequestModel
    private var recordApiManager = RecordApiManager()

    private val mainViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MainViewModel::class.java]
    }

    override fun initStartView() {
        super.initStartView()

        name = prefs.getString("Dpnm", "홍길동")
        account = prefs.getString("FinAcno","")
        money = viewModel.remitLiveData.value!!.money
        remitValue = RemitRequestModel(account, money)
        ttsMoney = addCommasToNumber(viewModel.remitLiveData.value!!.money.toLong())
    }
    override fun initDataBinding() {
        super.initDataBinding()

        binding.tvRemitName.text = name
        binding.tvRemitBank.text = "농협은행"
        binding.tvRemitMoney.text = ttsMoney
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
        setUtil(resources.getString(R.string.RemitCheck_receiver_check, name, ttsMoney))
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
    fun onRemitEvent(event: RemitEvent){
        if(event.isSuccess){
            if(event.result.isRemit){
                RemitSuccessDialog().show(parentFragmentManager,"송금성공")
                dismiss()
            }else{
                customTTS.speak(resources.getString(R.string.Remit_low_balance))
                Thread.sleep(3000)
                dismiss()
                navController.navigate(R.id.action_remitFragment_to_mainFragment)
            }
        }else{
            customTTS.speak(resources.getString(R.string.no_network))
        }
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
                        recordApiManager.toRemitService(remitValue)
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