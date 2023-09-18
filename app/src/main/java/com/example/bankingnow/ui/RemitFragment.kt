package com.example.bankingnow.ui

import android.os.Handler
import android.os.Looper
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import com.example.bankingnow.R
import com.example.bankingnow.databinding.FragmentRemitBinding
import com.example.bankingnow.base.BaseFragment

class RemitFragment  : BaseFragment<FragmentRemitBinding>(R.layout.fragment_remit) {
    override fun initStartView() {
        super.initStartView()

        // 송금 금액 다이얼로그
        val dialog = RemitMoneyDialog()
        dialog.show(parentFragmentManager,"")
    }

    override fun initDataBinding() {
        super.initDataBinding()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setFragmentResultListener("Back") { _, bundle ->
            val result = bundle.get("isSuccess") as Boolean
            if (result) {
                requireActivity().onBackPressed()
            } else{
                requireActivity().onBackPressed()
            }
        }
    }
}