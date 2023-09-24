package com.example.bankingnow.util

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.model.RecordModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Thread.sleep

// 녹음시작 -> 3초후 중단 -> 녹음 데이터 서버로 보냄 -> -결과값을 받아옴 -> 다시 녹음시작
class Recorder {
    // RELEASE(쉬고있는 상태) -> RECORDING -> RELEASE
    private enum class State {
        RELEASE, RECORDING
    }

    private var recorder: MediaRecorder? = null
    private var state: State = State.RELEASE

    private var apiManager: RecordApiManager = RecordApiManager()

    fun startOneRecord(filename: String, isPublic: Boolean = false) {
        state = State.RECORDING

        // MediaRecorder 객체 초기화 및 설정
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS) // or MediaRecorder.OutputFormat.MPEG_4
            setOutputFile(filename)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // or MediaRecorder.AudioEncoder.DEFAULT
            setAudioSamplingRate(44100) // set the desired sampling rate
            setAudioEncodingBitRate(320000)
            setMaxDuration(2500)

            try {
                prepare()
                Log.d("RECORD!", "START")
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed $e")
            }

            sleep(200)
            start()
        }
        // 2.5초 후에 녹음 중단 및 서버 전송
        Handler().postDelayed({
            stopRecording()
            sendFileToServer(filename, isPublic)
        }, 2500)
    }



    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }

        recorder = null
        state = State.RELEASE
    }


    private fun mediaRecorderToByteArray(outputFile: String): ByteArray? {
        val file = File(outputFile)
        if (!file.exists()) {
            return null
        }

        val inputStream = FileInputStream(file)
        val buffer = ByteArrayOutputStream()

        inputStream.use { input ->
            buffer.use { output ->
                val data = ByteArray(1024)
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }
                output.flush()
            }
        }

        return buffer.toByteArray()
    }

    private fun sendFileToServer(filename: String, isPublic: Boolean) {
        // 서버 전송
        val byteArray = mediaRecorderToByteArray(filename)

        // true: 숫자 정보 공개(음성 안내), false: 숫자 정보 비공개(진동 안내)
        byteArray?.let { RecordModel(it) }?.let { apiManager.postNumber(it, isPublic) }
    }
}


