package com.example.bankingnow.ui

import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.FragmentRemitBinding
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.event.RemitEvent
import com.example.bankingnow.model.RemitRequestModel
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RemitFragment  : BaseFragment<FragmentRemitBinding>(R.layout.fragment_remit) {
    private var apiManager: RecordApiManager = RecordApiManager()
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }
    private var remitResult = RemitRequestModel()

    override fun initStartView() {
        super.initStartView()

        binding.tvLoading.visibility = View.GONE
        // 송금 금액 다이얼로그
        RemitMoneyDialog().show(parentFragmentManager,"")
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        viewModel.remitLiveData.observeForever {
            remitResult = it
        }

        setFragmentResultListener("Back") { _, bundle ->
            binding.tvLoading.visibility = View.GONE
            val result = bundle.get("isSuccess") as Boolean
            if (result) {
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressed()
            }
        }

        setFragmentResultListener("Check") { _, bundle ->
            binding.tvLoading.visibility = View.VISIBLE
            val result = bundle.get("isFill") as Boolean
            if (result) {
                // 서버 통신 코드 구현
                // 계좌 정보가 유효 하다면 CheckDialog show
                RemitCheckDialog().show(parentFragmentManager, "")
            } else {
                customTTS.speak(resources.getString(R.string.RemitFragment_noRemit))
                RemitMoneyDialog().show(parentFragmentManager,"")
            }
        }

        setFragmentResultListener("ReCheck") { _, _ ->
            binding.tvLoading.visibility = View.VISIBLE
            RemitCheckDialog().show(parentFragmentManager, "")
        }
    }
}

