package com.example.bankingnow.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.databinding.FragmentBalanceBinding
import com.example.bankingnow.model.GetBalanceModel
import java.text.NumberFormat
import java.util.Locale

class BalanceFragment : BaseFragment<FragmentBalanceBinding>(R.layout.fragment_balance),
    RecordApiManager.getMyBalance {
    private val TTS_ID = "TTS"

    private val apiManager = RecordApiManager()

    override fun initStartView() {
        super.initStartView()


        apiManager.listener = this
        apiManager.getBalance()
    }

    override fun initDataBinding() {
        super.initDataBinding()

    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()

    }

    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.fragmentBalance.setOnTouchListener { _, event ->
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
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }
                        requireActivity().onBackPressed()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }
                        apiManager.getBalance()
                    }
                }
            }
            true // 이벤트 소비
        }
    }

    override fun getBalance(balanceModel: GetBalanceModel) {
        Log.d("잔액확인", balanceModel.toString())
        binding.tvUserInfo.text = "${balanceModel.user_id} 님\n${balanceModel.bank_name} 통장잔액"
        binding.tvBalance.text = addCommasToNumber(balanceModel.balance) + " 원"


        val formattedString = getString(R.string.account_balance, balanceModel.user_id, balanceModel.bank_name, balanceModel.balance.toString())
        customTTS.speak(formattedString)

    }

    fun addCommasToNumber(number: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }

}
