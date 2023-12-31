package com.example.bankingnow.ui

import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.MyApplication
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.databinding.FragmentLoginBinding
import com.example.bankingnow.event.DrawStopEvent
import com.example.bankingnow.event.LoginEvent
import com.example.bankingnow.event.NumberPrivateEvent
import com.example.bankingnow.util.CustomVibrator
import com.example.bankingnow.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date
import kotlin.system.exitProcess

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private val stateList: Array<String> = arrayOf("START", "RECORD_START")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private var recordApiManager = RecordApiManager()

    private val ImageViewList : ArrayList<ImageView> = ArrayList()
    private val result: MutableLiveData<String> = MutableLiveData("")

    private var i = 0

    private val mainViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MainViewModel::class.java]
    }

    override fun initStartView() {
        ImageViewList.add(binding.ivPw1)
        ImageViewList.add(binding.ivPw2)
        ImageViewList.add(binding.ivPw3)
        ImageViewList.add(binding.ivPw4)
        ImageViewList.add(binding.ivPw5)
        ImageViewList.add(binding.ivPw6)

        mainViewModel.initModel()

        mainViewModel.num.observe(viewLifecycleOwner){
            Log.d("pw_num", it)
            i += 1

            if (idx.value == 1 && result.value!!.length <= 6) {
                result.value = result.value + it
                setFillCircle(result.value!!.length)
                if (result.value!!.length < 6) {
                    customVibrator?.vibratePhone()
                } else {
                    idx.postValue(0)
                }
            }
        }

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
            if (it.length==6) {
                // DrawView 종료
                EventBus.getDefault().post(DrawStopEvent())
                recordApiManager.toLoginService(it)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.isSuccess) {
            if (event.result.isLogin) {
                MyApplication.prefs.setBoolean("isLogin", true)
                requireActivity().onBackPressed()
            } else {
                customTTS.speak(resources.getString(R.string.not_correct_pw))
                resetCircle()
                idx.postValue(0)
            }
        } else {
            customTTS.speak(resources.getString(R.string.no_network))
            resetCircle()
            idx.postValue(0)
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
                                customTTS.tts.stop()
                                idx.postValue(1)
                                result.value = ""

                                DrawDialog().show(parentFragmentManager, "")
                                // result.value = "123456"

                                // 테스트
//                                MyApplication.prefs.setBoolean("isLogin", true)
//                                requireActivity().onBackPressed()
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