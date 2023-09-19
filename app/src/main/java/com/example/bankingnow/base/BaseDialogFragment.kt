package com.example.bankingnow.base

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.bankingnow.util.CustomTTS
import com.example.bankingnow.util.CustomVibrator

abstract class BaseDialogFragment <B: ViewDataBinding> (@LayoutRes private  val layoutResourceId: Int) :
    DialogFragment() {
    // protected abstract val viewModel: VM
    protected lateinit var binding: B
    protected lateinit var navController: NavController

    protected var lastTouchTime: Long = 0
    protected val doubleClickDelay: Long = 500 // 더블 클릭 간격 설정 (0.5초)
    protected var isSingleClick = false

    protected var customVibrator: CustomVibrator? = null
    protected lateinit var customTTS: CustomTTS
    protected var vibrator: Vibrator? = null
    protected lateinit var tts: TextToSpeech


    // * 레이아웃을 띄운 직후 호출. * 뷰나 액티비티의 속성 등을 초기화. * ex) 리사이클러뷰, 툴바, 드로어뷰..
    protected open fun initStartView() {}
    // * 데이터 바인딩 설정.
    protected open fun initDataBinding() {}
    // * 바인딩 이후에 할 일을 여기에 구현. * 그 외에 설정할 것이 있으면 이곳에서 설정. * 클릭 리스너도 이곳에서 설정.
    protected open fun initAfterBinding() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // false : 화면 밖 터치 혹은 뒤로가기 버튼 누를 시 dismiss 안됨
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 둥근 모서리 적용
        //dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE) //android version 4.4 이하에서 blue line 생기는거 방지
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        setUtil()

        initStartView()
        initDataBinding()
        initAfterBinding()
    }

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onDestroy() {
        super.onDestroy()

        // vibrator = null

        if (customTTS.tts.isSpeaking) {
            tts.stop()
        }
        // tts.shutdown()
    }

    private fun setUtil() {
        customTTS = CustomTTS.getInstance(requireContext())
        customVibrator = CustomVibrator.getInstance(requireContext())

        tts = customTTS.tts
        vibrator = customVibrator!!.vibrator
    }

    // 다이얼로그 크기
    fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialogFragment.dialog?.window
            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    /* 다이얼로그 프래그먼트에서 사용시.
    * override fun onResume() {
	* 	super.onResume()
	* 	context?.dialogFragmentResize(this@CustomDialogFragment, 0.9f, 0.9f)
	* }
    * */
}