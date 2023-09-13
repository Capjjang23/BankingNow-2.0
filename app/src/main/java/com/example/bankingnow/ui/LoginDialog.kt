package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.writenow.base.BaseDialogFragment
import java.util.Date
import java.util.Locale

class LoginDialog: BaseDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"

    private var recorder = Recorder()
    private var recordApiManager = RecordApiManager()


    override fun initAfterBinding() {
        super.initAfterBinding()
        Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
        // setTTS 함수 실행
        customTTS.speak("비밀번호를 입력해주세요. 입력을 시작하려면 화면을 한번 터치해주세요.")

        binding.dialogLogin.setOnClickListener{
            prefs.setBoolean("isLogin", true)
            Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
            dismiss()

//            recorder.startRecording(filePath)
            // 클릭 리스너를 제거하여 두 번째 클릭부터는 실행되지 않도록 함
//            binding.dialogLogin.setOnClickListener(null)

            // recordApiManager.checkPW("123456")
        }
    }

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}