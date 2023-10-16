/**
 * Copyright @marcosscarpim.
 */

package com.example.bankingnow.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scarpim.digitclassifier.classifier.Classifier
import com.scarpim.digitclassifier.classifier.Recognition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel(application: Application): AndroidViewModel(application) {

    private var classifier: Classifier? = null

    private var _num = MutableLiveData<String>()

    val num:LiveData<String>
        get() = _num

    private val _result: MutableStateFlow<Recognition?> = MutableStateFlow(null)
    val result: StateFlow<Recognition?>
        get() = _result


    fun setNum(string: String){
        Log.d("numnum", "update")
        _num.value=string
    }
    fun saveFile(filepath: String){

        // 파일 경로로부터 Bitmap 로드
        val bitmap: Bitmap = BitmapFactory.decodeFile(filepath)

        if (bitmap != null) {
            // 로드된 비트맵을 사용하거나 표시하십시오.
            Log.d("bitbit",bitmap.toString())
        } else {
            // Bitmap 로드 실패 또는 파일이 없을 경우 처리할 내용을 여기에 추가합니다.
        }



    }
    fun initModel() {
        if (classifier == null) {
            viewModelScope.launch {
                try {
                    classifier = Classifier(getApplication())
                    Log.v("MarcosLog", "Classifier initialized")
                } catch (e: IOException) {
                    Log.e("MarcosLog", "init(): Failed to create Classifier", e)
                }
            }
        }
    }

    fun classify(bitmap: Bitmap) {
        viewModelScope.launch {
            // TODO HARDCODED values knowing the size of my model. This is not ideal...
            val scaled = bitmap.scaleBitmap(28, 28)
            _result.emit(classifier?.classify(scaled))
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier?.close()
    }
}

fun Bitmap.scaleBitmap(width: Int, height: Int): Bitmap {
    val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    this.recycle()
    return scaledBitmap
}
