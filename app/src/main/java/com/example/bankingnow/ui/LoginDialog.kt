package com.example.bankingnow.ui

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bankingnow.R
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.writenow.base.BaseDialogFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.Locale

class LoginDialog: BaseDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    private var lastTouchTime: Long = 0
    private val doubleClickDelay: Long = 500 // 더블 클릭 간격 설정 (0.5초)
    private lateinit var tts: TextToSpeech
    private val TTS_ID = "TTS"

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTTS()

        binding.dialogLogin.setOnClickListener{
            (activity as MainActivity).setIsLogin()

            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun setTTS() {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status!=TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
                tts.setPitch(1.0f)
                tts.setSpeechRate(1.0f)
                Log.d("TTS INIT", "SUCCESS")
            }
            else{
                Log.d("TTS INIT", "FAIL")
            }
        })
    }
}