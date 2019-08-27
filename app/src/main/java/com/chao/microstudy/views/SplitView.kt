package com.chao.microstudy.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


/**
 * Date: 2019/8/26 19:21
 * Author: hans yang
 * Description:
 */
class SplitView (context: Context) : View(context,null) {

    data class Ball(var color: Int,var x: Float,var y:Float,var r: Float,
                    var vx: Float,var vy:Float,var ax: Float,var ay:Float)

    val d = 3f
    var mPaint: Paint = Paint()
    var mBitmap: Bitmap

    var mValueAnimator: ValueAnimator
    var mList = ArrayList<Ball>()

    init {
        mPaint.isAntiAlias = true
        mBitmap = BitmapFactory.decodeResource(resources, com.chao.microstudy.R.mipmap.ic_directed_avator)

        for (i in 0 until mBitmap.width){
            for (j in 0 until mBitmap.height){
                val color = mBitmap.getPixel(i,j)
                val r = d / 2
                val x = i * d + r
                val y = j * d + r

//                val vx = ((-1.0).pow(ceil(Math.random() * 1000)) * 20f * Math.random()).toFloat()
//                val vy = rangInt(-15,35).toFloat()
                val vx = 10 - Math.random().toFloat() * 20f
                val vy = 10 - Math.random().toFloat() * 20f
                val ax = 0f
                val ay = 0.98f

                val ball = Ball(color,x,y,r,vx,vy,ax,ay)
                mList.add(ball)
            }
        }

        mValueAnimator = ValueAnimator.ofFloat(0f,1f).apply {
            duration = 2000
            repeatCount = -1
            addUpdateListener {
                updateBall()
                invalidate()
            }
        }
    }

    private fun rangInt(i: Int, j: Int): Int {
        val max = max(i, j)
        val min = min(i, j) - 1
        //在0到(max - min)范围内变化，取大于x的最小整数 再随机
        return (min + ceil(Math.random() * (max - min))).toInt()
    }

    private fun updateBall() {
        mList.forEach {
            it.x += it.vx
            it.y += it.vy

            it.vx += it.ax
            it.vy += it.ay
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(200f,200f)
        mList.forEach {
            mPaint.color = it.color
            canvas.drawCircle(it.x,it.y,it.r,mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN){
            mValueAnimator.start()
        }
        return super.onTouchEvent(event)
    }

}