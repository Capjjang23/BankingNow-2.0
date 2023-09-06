package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitCheckBinding
import com.example.bankingnow.databinding.DialogRemitPasswordBinding
import com.example.writenow.base.BaseDialogFragment

class RemitCheckDialog : BaseDialogFragment<DialogRemitCheckBinding>(R.layout.dialog_remit_check) {

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        binding.dialogRemitCheck.setOnClickListener {
            RemitPasswordDialog().show(parentFragmentManager,"비밀 번호")
            this.dismiss()
        }
    }
}