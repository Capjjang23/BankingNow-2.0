package com.example.bankingnow.ui

import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.FragmentMainBinding
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.event.DrawStopEvent
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Thread.sleep
import java.util.Date
import kotlin.system.exitProcess

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private val stateList: Array<String> = arrayOf("START", "RECORD_START")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val mainViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MainViewModel::class.java]
    }

    override fun initStartView() {
        super.initStartView()

        Log.d("pw_MAIN","")
        // 테스트
        // prefs.setBoolean("isLogin", true)

        // 송금 금액 다이얼로그
        if (!prefs.getBoolean("isLogin", false)) {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }

        mainViewModel.initModel()

        mainViewModel.num.observe(viewLifecycleOwner){
            Log.d("pw_num", it)
            idx.postValue(0)
            EventBus.getDefault().post(DrawStopEvent())

            when(it){
                "init" -> {}
                "1" -> {
                    customTTS.speak(resources.getString(R.string.Main_choose_one))
                    sleep(3000)
                    navController.navigate(R.id.action_mainFragment_to_balanceFragment)
                }
                else ->{
                    customTTS.speak(resources.getString(R.string.Main_choose_two))
                    sleep(3000)
                    navController.navigate(R.id.action_mainFragment_to_remitFragment)
                }
            }
        }
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
        setUtil(resources.getString(R.string.Main_choose_info) + resources.getString(R.string.record_start))

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }
    }

    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.fragmentMain.setOnTouchListener { _, event ->
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
                        customTTS.tts.stop()
                        exitApp()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        when (state) {
                            "START" -> {
                                customTTS.tts.stop()
                                idx.postValue(1)

                                DrawDialog().show(parentFragmentManager, "")

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