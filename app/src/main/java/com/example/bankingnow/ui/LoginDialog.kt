package com.example.bankingnow.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.MyApplication.Companion.prefs
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.bankingnow.R
import com.example.bankingnow.Recorder
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.writenow.base.BaseDialogFragment
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class LoginDialog: BaseDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"

    private var recorder = Recorder()
    private var recordApiManager = RecordApiManager()

    private val ImageViewList : ArrayList<ImageView> = ArrayList()

    override fun initStartView() {
        ImageViewList.add(binding.ivPw6)
        ImageViewList.add(binding.ivPw5)
        ImageViewList.add(binding.ivPw4)
        ImageViewList.add(binding.ivPw3)
        ImageViewList.add(binding.ivPw2)
        ImageViewList.add(binding.ivPw1)

    }

    override fun initAfterBinding() {
        super.initAfterBinding()
        Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
        // setTTS 함수 실행
        customTTS.speak("비밀번호를 입력해주세요. 입력을 시작하려면 화면을 한번 터치해주세요.")

        setFillCircle(0)

        binding.dialogLogin.setOnClickListener{
            prefs.setBoolean("isLogin", true)
            Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
            dismiss()

            recorder.startRecording(filePath)
            // 클릭 리스너를 제거하여 두 번째 클릭부터는 실행되지 않도록 함
            binding.dialogLogin.setOnClickListener(null)

            recordApiManager.checkPW("capjjang1234")
        }
    }

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun setTTS(message: String) {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status!=TextToSpeech.ERROR){
                tts.language = Locale.KOREAN
                tts.setPitch(0.9f)
                tts.setSpeechRate(1.0f)

                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
//                delay(1000)
                Log.d("TTS INIT", "SUCCESS")
            }
            else{
                Log.d("TTS INIT", "FAIL")
            }
        })
    }

    private fun setFillCircle(index:Int){
        for (i in 1..index){
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fill_circle) }
            ImageViewList[i].setImageDrawable(drawable)
        }
    }
}