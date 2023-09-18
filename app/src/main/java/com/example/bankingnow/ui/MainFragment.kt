package com.example.bankingnow.ui

import android.util.Log
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.databinding.FragmentMainBinding
import com.example.bankingnow.databinding.FragmentRemitBinding
import com.example.bankingnow.base.BaseFragment
import kotlin.system.exitProcess

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    override fun initStartView() {
        super.initStartView()

        // 송금 금액 다이얼로그
        if (!prefs.getBoolean("isLogin", false)) {
            Log.d("isLogin?: ", prefs.getBoolean("isLogin", false).toString())
            LoginDialog().show(parentFragmentManager,"")
        }

    }

    override fun initDataBinding() {
        super.initDataBinding()


    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

        binding.btnBalance.setOnClickListener{
            customVibrator?.vibratePhone()
            navController.navigate(R.id.action_mainFragment_to_balanceFragment)
        }
        binding.btnRemit.setOnClickListener{
            customVibrator?.vibratePhone()
            navController.navigate(R.id.action_mainFragment_to_remitFragment)
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
                    if (distanceX < -100) {
                        // 왼쪽으로 스와이프
                        exitApp()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
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