package com.example.writenow.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<B : ViewDataBinding>(@LayoutRes private val layoutResourceId: Int) :
    Fragment() {
    protected lateinit var binding: B
    protected lateinit var navController: NavController

    // * 레이아웃을 띄운 직후 호출. * 뷰나 액티비티의 속성 등을 초기화. * ex) 리사이클러뷰, 툴바, 드로어뷰..
    protected open fun initStartView() {}
    // * 데이터 바인딩 설정.
    protected open fun initDataBinding() {}
    // * 바인딩 이후에 할 일을 여기에 구현. * 그 외에 설정할 것이 있으면 이곳에서 설정. * 클릭 리스너도 이곳에서 설정.
    protected open fun initAfterBinding() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        initStartView()
        initDataBinding()
        initAfterBinding()

    }

    fun vibratePhone() {
        val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Android 26 (Oreo) 버전 이상에서는 VibrationEffect를 사용합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE) // 1000ms(1초) 동안 진동
            vibrator.vibrate(vibrationEffect)
        } else {
            // Android 25 (Nougat) 이하에서는 deprecated된 vibrate() 메서드를 사용합니다.
            vibrator.vibrate(300) // 1000ms(1초) 동안 진동
        }
    }

}