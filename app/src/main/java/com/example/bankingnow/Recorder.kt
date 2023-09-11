package com.example.bankingnow

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.bankingnow.apiManager.RecordApiManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

// 녹음시작 -> 3초후 중단 -> 녹음 데이터 서버로 보냄 -> -결과값을 받아옴 -> 다시 녹음시작
class Recorder {

    // 릴리즈(쉬고있는 상태) -> 녹음중 -> 릴리즈
    private enum class State {
        RELEASE, RECORDING
    }

    private var recorder: MediaRecorder? = null
    private var filename: String = ""
    private var state: State = State.RELEASE

    private var apiManager:RecordApiManager = RecordApiManager()

    private fun onRecord(start: Boolean) = if (start) startRecording() else stopRecording()

    private fun startRecording() {
        state = State.RECORDING

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS) // or MediaRecorder.OutputFormat.MPEG_4
            setOutputFile(filename)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // or MediaRecorder.AudioEncoder.DEFAULT
            setAudioSamplingRate(44100) // set the desired sampling rate
            setAudioEncodingBitRate(320000)
            setMaxDuration(1500)


            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed $e")
            }

            start()

            Thread.sleep(1500)

            sendFileToServer(filename)
        }
    }
    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }

        recorder = null
        state = State.RELEASE
    }

    fun mediaRecorderToByteArray(outputFile: String): ByteArray? {
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

    fun sendFileToServer(filename:String){
        // 서버 전송
        Log.d("[mmihye]","녹음 멈춤 & 서버 전송")
        val byteArray = mediaRecorderToByteArray(filename)
//        val recordModel = byteArray?.let { RecordModel(it) }
//        if (recordModel != null) {
//            apiManager.postTest(recordModel)
//        }
    }


}