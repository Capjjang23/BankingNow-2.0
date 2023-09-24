package com.example.bankingnow.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
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
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.R
import com.example.bankingnow.util.Recorder
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.bankingnow.util.CustomTTS.Companion.TTS_ID
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.event.LoginEvent
import com.example.bankingnow.event.NumberPrivateEvent
import com.example.bankingnow.event.NumberPublicEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class LoginDialog: BaseDialogFragment<DialogLoginBinding>(R.layout.dialog_login) {
    private val stateList: Array<String> = arrayOf("START", "RECORD_START")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"

    private var recorder = Recorder()
    private var recordApiManager = RecordApiManager()

    private val ImageViewList : ArrayList<ImageView> = ArrayList()

    private val isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    private val result: MutableLiveData<String> = MutableLiveData("")

    override fun initStartView() {
        ImageViewList.add(binding.ivPw1)
        ImageViewList.add(binding.ivPw2)
        ImageViewList.add(binding.ivPw3)
        ImageViewList.add(binding.ivPw4)
        ImageViewList.add(binding.ivPw5)
        ImageViewList.add(binding.ivPw6)

        // setTTS 함수 실행
        setUtil(resources.getString(R.string.app_start) + resources.getString(R.string.Login_info) + resources.getString(R.string.Password_info))
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }

        result.observe(viewLifecycleOwner) {
            if (it.length ==6) {
                recorder.stopRecording()
                recordApiManager.checkPW(it)
                Log.d("pw_result", it)
            }
        }

        setFillCircle(0)
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 다이얼로그가 dismiss될 때 실행할 코드
        customTTS.speak(resources.getString(R.string.Main_choose_info) + resources.getString(R.string.record_start))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNumberEvent(event: NumberPrivateEvent) {
        if (event.isSuccess) {
            isResponse.postValue(true)
            customVibrator?.vibratePhone()
            result.value = result.value + event.result.predicted_number
            setFillCircle(result.value!!.length)

            if (result.value!!.length < 6) {
                recorder.startOneRecord(filePath, false)

                if (idx.value == 1 && result.value!!.length <= 6) {
                    customVibrator?.vibratePhone()
                    result.value = result.value + event.result.predicted_number
                    if (result.value!!.length < 6) {
                        recorder.startOneRecord(filePath, false)
                    }
                } else {
                    isResponse.postValue(false)
                    idx.postValue(0)
                }
            } else {
                isResponse.postValue(false)
                customTTS.speak(resources.getString(R.string.no_network))
                idx.postValue(0)
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        fun onLoginEvent(event: LoginEvent) {
            if (event.isSuccess) {
                if (event.result.is_password_correct) {
                    prefs.setBoolean("isLogin", true)
                    customTTS.speak("로그인 성공")
                    dismiss()
                } else {
                    customTTS.speak(resources.getString(R.string.not_correct_pw))
                    resetCircle()
                    idx.postValue(0)
                }
            } else {
                customTTS.speak(resources.getString(R.string.no_network))
                idx.postValue(0)
            }
        }
    }


    private fun setFillCircle(index:Int){
        for (i in 1..index){
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fill_circle) }
            ImageViewList[i-1].setImageDrawable(drawable)
        }
    }

    private fun resetCircle(){
        for (i in 1..6){
            val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.circle) }
            ImageViewList[i-1].setImageDrawable(drawable)
        }
    }

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
                    if (distanceX > 100) {
                        // 오른쪽으로 스와이프
                        exitApp()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        when (state) {
                            "START" -> {
                                idx.postValue(1)
                                result.value = ""
                                recorder.startOneRecord(filePath, false)

                                // 테스트
//                                prefs.setBoolean("isLogin", true)
//                                dismiss()
                            }
                        }
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