package com.example.bankingnow.ui

import android.os.Environment
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.FragmentMainBinding
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.util.Recorder
import com.example.bankingnow.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Thread.sleep
import java.util.Date
import kotlin.system.exitProcess

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"

    private var recorder = Recorder()
    private var recordApiManager = RecordApiManager()

    private val mainViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MainViewModel::class.java]
    }

    override fun initStartView() {
        super.initStartView()

//         송금 금액 다이얼로그
        if (!prefs.getBoolean("isLogin", false)) {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }

        // 테스트
        // prefs.setBoolean("isLogin", true)

         DrawDialog().show(parentFragmentManager, "DrawDialog")
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
        setUtil(resources.getString(R.string.Main_choose_info) + resources.getString(R.string.record_start))
        binding.btnBalance.setOnClickListener{
            customVibrator?.vibratePhone()
            navController.navigate(R.id.action_mainFragment_to_balanceFragment)
        }
        binding.btnRemit.setOnClickListener{
            customVibrator?.vibratePhone()
            navController.navigate(R.id.action_mainFragment_to_remitFragment)
        }
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
    fun NumberPublicEvent(event: NumberPublicEvent) {
        if (event.isSuccess){
            val num = event.result.predicted_number
            when(num){
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
                // else -> customTTS.speak(resources.getString(R.string.Main_choose_again))

            }
        } else{
            customTTS.speak(resources.getString(R.string.no_network) + resources.getString(R.string.record_start))
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
                        customTTS.tts.stop()
                        recorder.startOneRecord(filePath,true)
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