package com.salmaso.riccardo.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.widget.RelativeLayout

class CanvasDraw{

    class DrawX(context: Context,
                  val layout: RelativeLayout,
                  val startX: Float,
                  val nLine: Int) : View(context) {
        val startY = startX - ( 300  * nLine)
        var endX = startX
        var endY = startY

        //var nLine = nLine
        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            val paint = Paint()
            paint.setStrokeWidth(7f)
            canvas.drawLine(startX, startY, endX, endY, paint)
            if (endY < 300f + startY) { // set end points
                endY+=60
                if(nLine == 0)
                    endX+=60
                else
                    endX-=60
                postInvalidate() // set time here
            } else if(nLine == 0){
                endY = startX + 300f
                layout.addView(DrawX(context,
                        layout,
                        endY,
                        1
                ))
            }
        }
    }

    class DrawCircle(context: Context,
                       val margin: Float) : View(context) {
        var angle = 0f;

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            val paint = Paint()
            paint.setStrokeWidth(7f)
            val rectF = RectF(margin, margin, 400f, 400f)
            paint.style = Paint.Style.STROKE
            canvas.drawArc(rectF, 0f, angle, false, paint)
            if (angle != 360f) {
                angle+=36
                postInvalidate()
            }
        }
    }

    class DrawCamp1(context: Context,
                    val layout: RelativeLayout,
                    val widthDisplay: Float) : View(context) {
        var endY = 0f

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            val paint = Paint()
            paint.setStrokeWidth(7f)
            var distance:Float = widthDisplay / 3f
            canvas.drawLine(distance, 0f, distance, endY, paint)
            canvas.drawLine(0f, distance, endY, distance, paint)
            if (endY < widthDisplay) { // set end points
                endY+=50
                postInvalidate() // set time here
            } else{
                layout.addView(DrawCamp2(context, layout, widthDisplay))
            }
        }
    }

    class DrawCamp2(context: Context,
                    val layout: RelativeLayout,
                    val widthDisplay: Float) : View(context) {
        var endY = widthDisplay
        val distance = (widthDisplay/3) *2
        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            val paint = Paint()
            paint.setStrokeWidth(7f)
            canvas.drawLine(endY, distance, widthDisplay, distance, paint)
            canvas.drawLine(distance, endY, distance, widthDisplay, paint)
            if (endY > 0f) { // set end points
                endY-=50
                postInvalidate() // set time here
            }
        }
    }
}