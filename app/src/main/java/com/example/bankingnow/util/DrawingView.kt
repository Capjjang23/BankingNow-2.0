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
import com.example.bankingnow.MyApplication.Companion.prefs
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
        paint.strokeWidth = 50f
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

                // 롱 프레스 이벤트를 처리합니다.
                if (upEventTime - downEventTime > 1500) { // 1.5초 이상 롱 프레스로 판단
                    Toast.makeText(context, "숫자 입력을 마칩니다.", Toast.LENGTH_SHORT).show()
                } else{
                    // ACTION_UP 이벤트가 발생한 후 0.7초 이내에 ACTION_DOWN 이벤트가 발생하지 않으면 그림을 저장
                    val handler = Handler()
                    handler.postDelayed({
                        if(upEventTime>downEventTime){
                            savedBitmap = getDrawingBitmap()
                            saveBitmapToImage(savedBitmap!!)
                        }
                    }, 700)
                }

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
        val longEdgeSize = if (width >= height) width else height

        val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/num.png" // 저장할 파일 경로 및 파일명
        val file = File(filePath)

        try {
            val fileOutputStream = FileOutputStream(file)
            val resizedBitmap = resizeBitmapToLongEdge(bitmap, longEdgeSize)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
            clearDrawing()
            Toast.makeText(context, "그림이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "그림을 저장하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resizeBitmapToLongEdge(bitmap: Bitmap, longEdgeSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val max = if(width>height) width else height

        val resultBitmap = Bitmap.createBitmap(max, max, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 이미지를 중앙에 그릴 위치 계산
        val left = (max - width) / 2
        val top = (max - height) / 2

        // 이미지를 중앙에 그리고 나머지 영역을 투명하게 만듭니다.
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, max.toFloat(), max.toFloat(), paint)
        canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), null)

        return resultBitmap
    }

    fun clearDrawing() {
        path.reset()
        savedBitmap = null // 그림 초기화
        invalidate()
    }
}
