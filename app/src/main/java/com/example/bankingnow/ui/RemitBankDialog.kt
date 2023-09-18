package com.example.bankingnow.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogLoginBinding
import com.example.bankingnow.databinding.DialogRemitBankBinding
import com.example.bankingnow.util.Recorder
import com.example.writenow.base.BaseDialogFragment
import java.util.Date
import java.util.Locale

class RemitBankDialog : BaseDialogFragment<DialogRemitBankBinding>(R.layout.dialog_remit_bank) {
    private val handler = Handler()
    private lateinit var speechRecognizer: SpeechRecognizer

    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()
    private var recordApiManager = RecordApiManager()

    override fun onResume() {
        super.onResume()

        // dialog full Screen code
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun initStartView() {
        super.initStartView()
        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "bankingnow")    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정


        customTTS.speak("송금하실 은행을 말씀해주세요. 녹음을 시작하려면 화면을 한번 터치해주세요.")

        binding.dialogRemitBank.setOnClickListener {

            recordApiManager.getBank("국빈은행")
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
//            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
//            speechRecognizer.startListening(intent) //듣기시작

        }

    }

    override fun initAfterBinding() {
        super.initAfterBinding()

//        setTouchScreen()

//        binding.dialogRemitBank.setOnClickListener {
//            RemitAccountDialog().show(parentFragmentManager,"계좌 번호")
//            this.dismiss()
//        }
    }


    private fun setTouchScreen() {
        binding.dialogRemitBank.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isSingleClick) {
                        // 더블 클릭 처리: 뒤로 가기
                        RemitMoneyDialog().show(parentFragmentManager,"계좌 번호")
                        dismiss()
                    } else {
                        // 첫 번째 클릭 시작
                        isSingleClick = true
                        handler.postDelayed({
                            if (isSingleClick) {
                                // 한 번 클릭 처리: Log 출력
                                RemitAccountDialog().show(parentFragmentManager,"송금")
                                dismiss()
                            }
                            isSingleClick = false
                        }, doubleClickDelay)
                    }
                }
            }
            true
        }
    }
}

// 리스너 설정
private val recognitionListener: RecognitionListener = object : RecognitionListener {
    // 말하기 시작할 준비가되면 호출
    override fun onReadyForSpeech(params: Bundle) {
        Log.d("bankSpeech", "음성인식 시작")
    }
    // 말하기 시작했을 때 호출
    override fun onBeginningOfSpeech() {
        Log.d("bankSpeech", "말하기 시작")
    }
    // 입력받는 소리의 크기를 알려줌
    override fun onRmsChanged(rmsdB: Float) {}
    // 말을 시작하고 인식이 된 단어를 buffer에 담음
    override fun onBufferReceived(buffer: ByteArray) {}
    // 말하기를 중지하면 호출
    override fun onEndOfSpeech() {
        Log.d("bankSpeech", "말하기 중지")
    }
    // 오류 발생했을 때 호출
    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
            SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
            SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
            SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
            SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
            else -> "알 수 없는 오류임"
        }
        Log.d("bankSpeech", "에러 $error")
    }
    // 인식 결과가 준비되면 호출
    override fun onResults(results: Bundle) {
        // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        for (i in matches!!.indices)
            Log.d("bankSpeech", "$matches")
    }
    // 부분 인식 결과를 사용할 수 있을 때 호출
    override fun onPartialResults(partialResults: Bundle) {}
    // 향후 이벤트를 추가하기 위해 예약
    override fun onEvent(eventType: Int, params: Bundle) {}
}
