package com.example.bankingnow.util

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var downEventTime: Long = 0

    private var path = Path()
    private var paint = Paint()
    private var savedBitmap: Bitmap? = null

    init {
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downEventTime = System.currentTimeMillis() // ACTION_DOWN 이벤트 발생 시간 저장
                path.moveTo(x, y)
                Log.d("DOWN!", "$x$y")
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                Log.d("MOVE!", "$x$y")
            }
            MotionEvent.ACTION_UP -> {
                val upEventTime = System.currentTimeMillis()

                // ACTION_UP 이벤트가 발생한 후 0.5초 이내에 ACTION_DOWN 이벤트가 발생하지 않으면 그림을 저장
                val handler = Handler()
                handler.postDelayed({
                    if(upEventTime - downEventTime>500){
                        savedBitmap = getDrawingBitmap()
                        saveBitmapToImage(savedBitmap!!)
                    }
                }, 500)

                return true
            }
            else -> return false
        }

        // View 다시 그리기
        invalidate()
        return true
    }

    private fun getDrawingBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    private fun saveBitmapToImage(bitmap: Bitmap) {
        val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/image.png" // 저장할 파일 경로 및 파일명
        val file = File(filePath)

        try {
            val fileOutputStream = FileOutputStream(file)
            val cropBitmap = toSquare(bitmap) // 1:1 비율로 저장하기 위해
            cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
            Toast.makeText(context, "그림이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "그림을 저장하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val size = if (width >= height) height else width
        val x = (width - size) / 2
        val y = (height - size) / 2

        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    fun clearDrawing() {
        path.reset()
        savedBitmap = null // 그림 초기화
        invalidate()
    }
}
