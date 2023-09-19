package com.example.bankingnow.ui

import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.FragmentRemitBinding
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.event.UserNameEvent
import com.example.bankingnow.model.RemitCheckModel
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RemitFragment  : BaseFragment<FragmentRemitBinding>(R.layout.fragment_remit) {
    private var apiManager: RecordApiManager = RecordApiManager()
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }
    private var remitResult = RemitCheckModel()

    override fun initStartView() {
        super.initStartView()

        // 송금 금액 다이얼로그
        RemitMoneyDialog().show(parentFragmentManager,"")
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        viewModel.remitLiveData.observeForever {
            remitResult = it
        }

        setFragmentResultListener("Back") { _, bundle ->
            val result = bundle.get("isSuccess") as Boolean
            if (result) {
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressed()
            }
        }

        setFragmentResultListener("Check") { _, bundle ->
            val result = bundle.get("isFill") as Boolean
            if (result) {
                // 서버 통신 코드 구현
                // 계좌 정보가 유효 하다면 CheckDialog show
                remitResult = viewModel.remitLiveData.value!!
                apiManager.postUserName(remitResult.user)
            } else {
                customTTS.speak(resources.getString(R.string.RemitFragment_noRemit))
                requireActivity().onBackPressed()
            }
        }

        setFragmentResultListener("ReCheck") { _, _ ->
            RemitCheckDialog(remitResult).show(parentFragmentManager, "")
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
    fun onUserNameEvent(event: UserNameEvent) {
        if (event.isSuccess){
            viewModel.remitLiveData.value!!.name = event.result.name
            RemitCheckDialog(remitResult).show(parentFragmentManager, "")
        } else {
            customTTS.speak(resources.getString(R.string.RemitFragment_noReceiver))
            requireActivity().onBackPressed()
        }
    }
}