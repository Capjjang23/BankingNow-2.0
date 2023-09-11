package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.databinding.DialogRemitPasswordBinding
import com.example.writenow.base.BaseDialogFragment

class RemitPasswordDialog : BaseDialogFragment<DialogRemitPasswordBinding>(R.layout.dialog_remit_password) {

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun initStartView() {
        super.initStartView()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        // 비밀번호 맞으면 송금완료 화면 띄우기
//        RemitSuccessDialog().show(parentFragmentManager,"송금 완료")
//        this.dismiss()
    }
}