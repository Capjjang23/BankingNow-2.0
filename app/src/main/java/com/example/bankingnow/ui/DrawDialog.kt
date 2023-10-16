package com.example.bankingnow.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.Paint
import android.util.Log
import android.graphics.ColorMatrixColorFilter
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import com.example.bankingnow.R
import com.example.bankingnow.base.BaseDialogFragment
import com.example.bankingnow.databinding.DialogDrawBinding
import com.example.bankingnow.model.MainViewModel
import com.example.bankingnow.util.DrawingView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DrawDialog: BaseDialogFragment<DialogDrawBinding>(R.layout.dialog_draw) {
    private lateinit var view_draw: DrawingView

    private lateinit var viewModel: MainViewModel

    override fun initStartView() {
        super.initStartView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.initModel()

        view_draw = binding.viewDraw

    }

    fun invertGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 밝기를 반전하기 위한 ColorMatrix 생성
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f, // 빨강 색상 반전
                0f, -1f, 0f, 0f, 255f, // 초록 색상 반전
                0f, 0f, -1f, 0f, 255f, // 파랑 색상 반전
                0f, 0f, 0f, 1f, 0f // 알파 채널 유지
            )
        )

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        // 반전된 이미지를 그릴 Bitmap 생성
        val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(invertedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return invertedBitmap
    }

}