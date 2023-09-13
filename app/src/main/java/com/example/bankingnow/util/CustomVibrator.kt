package com.example.bankingnow.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class CustomVibrator(mContext: Context) {
    var vibrator: Vibrator?

    companion object { // singleton object
        var instance: CustomVibrator? = null
        fun getInstance(context: Context): CustomVibrator {
            if (instance == null) {
                @Synchronized
                if (instance == null)
                    instance = CustomVibrator(context)
            }
            return instance as CustomVibrator
        }
    }

    init { // constructor
        vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibratePhone() {
        // Android 26 (Oreo) 버전 이상에서는 VibrationEffect를 사용합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE) // 1000ms(1초) 동안 진동
            vibrator?.vibrate(vibrationEffect)
        } else {
            // Android 25 (Nougat) 이하에서는 deprecated된 vibrate() 메서드를 사용합니다.
            vibrator?.vibrate(300) // 1000ms(1초) 동안 진동
        }
    }
}