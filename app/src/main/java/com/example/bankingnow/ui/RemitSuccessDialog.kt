package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogRemitSuccessBinding
import com.example.bankingnow.base.BaseDialogFragment
import java.util.Locale

class RemitSuccessDialog : BaseDialogFragment<DialogRemitSuccessBinding>(R.layout.dialog_remit_success) {
    override fun initAfterBinding() {
        super.initAfterBinding()

        setUtil(resources.getString(R.string.RemitSuccessDialog_info))

        binding.dialogRemitSuccess.setOnClickListener {
            setFragmentResult("Back", bundleOf("isSuccess" to true))
            dismiss()
        }
    }
}