package com.example.bankingnow.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bankingnow.MyApplication.Companion.prefs
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bankingnow.R
import com.example.bankingnow.util.Recorder
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.bankingnow.util.CustomTTS.Companion.TTS_ID
import com.example.bankingnow.base.BaseDialogFragment
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class LoginDialog: BaseDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"

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

        // setTTS 함수 실행
        customTTS.speak("비밀번호를 입력해주세요. 입력을 시작하려면 화면을 한번 터치해주세요.")

    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        setFillCircle(0)
    }

    private fun setFillCircle(index:Int){
        for (i in 1..index){
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fill_circle) }
            ImageViewList[i].setImageDrawable(drawable)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogLogin.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val distanceX = endX - startX

                    // 스와이프를 감지하기 위한 조건 설정
                    if (distanceX < -100) {
                        // 왼쪽으로 스와이프
                        exitApp()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
//                        prefs.setBoolean("isLogin", true)
//                        Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
//                        dismiss()
//                        recorder.startRecording(filePath)
//                        // 클릭 리스너를 제거하여 두 번째 클릭부터는 실행되지 않도록 함
//                        binding.dialogLogin.setOnClickListener(null)

//                        recordApiManager.checkPW("capjjang1234")
                    }
                }
            }
            true // 이벤트 소비
        }
    }

    private fun exitApp(){
        ActivityCompat.finishAffinity(requireActivity()) // 액티비티 종료
        exitProcess(0)
    }
}