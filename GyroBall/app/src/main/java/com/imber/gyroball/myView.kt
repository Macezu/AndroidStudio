package com.imber.gyroball

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi


class MyView(context: Context, attrs: AttributeSet): View(context, attrs)
{
    var leftTopX= -50f
    var leftTopY = -50f
    var rightBotX = +50f
    var rightBotY = +50f

    var xMove = 1f
    var yMove = 1f



    fun moveBall(){
        xMove = MainActivity.x
        yMove = MainActivity.y
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var width: Float = canvas.width/2.toFloat()
        var height: Float = canvas.height/2.toFloat()





        val paint = Paint()
        paint.color = Color.BLACK

            canvas.drawOval(
                    (leftTopX+width)+yMove,
                    (leftTopY+height)+xMove,
                    (rightBotX+width)+yMove,
                    (rightBotY+height) +xMove,
                    paint
            )


    }
    object vals{
        var x:Float = 0f
        var y: Float =0f
        var z: Float = 0f
    }



}