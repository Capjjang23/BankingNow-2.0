package com.example.bankingnow.ui

import android.content.Intent
import android.os.Environment
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Visibility
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.databinding.DialogRemitBankBinding
import com.example.bankingnow.util.Recorder
import java.util.Date
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.event.BankEvent
import com.example.bankingnow.viewmodel.RemitViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RemitBankDialog : BaseDialogFragment<DialogRemitBankBinding>(R.layout.dialog_remit_bank) {
    private val viewModel by lazy {
        ViewModelProvider(requireParentFragment())[RemitViewModel::class.java]
    }

    private lateinit var intent: Intent

    private val handler = Handler()
    private lateinit var speechRecognizer: SpeechRecognizer

    private val filePath =
        Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Date().time.toString() + ".aac"
    private var recorder = Recorder()

    private val stateList: Array<String> = arrayOf("FAIL", "SUCCESS")
    private val idx: MutableLiveData<Int> = MutableLiveData(0)
    private lateinit var state: String

    private val isResponse: MutableLiveData<Boolean> = MutableLiveData(false)
    private val result: MutableLiveData<String> = MutableLiveData()

    override fun initAfterBinding() {
        super.initStartView()

        setSTT()
        setTouchScreen()
        setUtil(resources.getString(R.string.RemitBank_info))

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정

        idx.observe(viewLifecycleOwner) {
            state = stateList[idx.value!!]
        }

        result.observe(viewLifecycleOwner){
            viewModel.setRemitBank(it)
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
    fun onBankEvent(event: BankEvent) {
        if (event.isSuccess) {
            isResponse.postValue(true)

            val formattedString = getString(R.string.RemitBank_remit_check, event.result.closest_bank)
            customTTS.speak(formattedString)

            result.postValue(event.result.closest_bank)
            binding.tvBank.text = event.result.closest_bank
            binding.bank.visibility = View.INVISIBLE
            state = "SUCCESS"
            prefs.setString("Account", event.result.closest_bank)

        } else {
            isResponse.postValue(false)
            customTTS.speak(resources.getString(R.string.no_network))
            idx.postValue(1)
        }
    }

    private fun setSTT() {
        // RecognizerIntent 생성
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "bankingnow")    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정
    }

    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.dialogRemitBank.setOnTouchListener { _, event ->
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

                        RemitMoneyDialog().show(parentFragmentManager, "송금 금액")
                        dismiss()
                    } else if (state == "SUCCESS" && distanceX < -100) {
                        // 왼쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        RemitAccountDialog().show(parentFragmentManager, "송금 계좌")
                        dismiss()
                    } else if (distanceX > -10 && distanceX < 10) {
                        // 클릭으로 처리
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }

                        when (state) {
                            "FAIL" -> {
                                idx.postValue(1)
                                // stt 구현
                                speechRecognizer.startListening(intent) //듣기시작
                            }

                            "RECORD_START" -> {
                                idx.postValue(2)
                            }

                            "SUCCESS" -> {
                                idx.postValue(1)
                                // stt 구현
                                speechRecognizer.startListening(intent) //듣기시작
                            }
                        }
                    }
                }
            }
            true // 이벤트 소비
        }
    }

    // 리스너 설정
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        private var recordApiManager = RecordApiManager()

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
            if (matches.isNullOrEmpty()) {
                customTTS.speak(resources.getString(R.string.RemitBank_Empty))
                customTTS.speak(resources.getString(R.string.RemitBank_info))
            } else
                recordApiManager.getBank(matches.toString())

        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}