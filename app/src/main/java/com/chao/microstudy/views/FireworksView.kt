package com.chao.microstudy.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * Date: 2019/8/27 10:15
 * Author: hans yang
 * Description:
 */
class FireworksView(context: Context) : View(context,null) {

    private val  TAG = "FireworksView"

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    private val mPaint2 by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
    }

    private var mSplitPaths = listOf(Path(),Path(),Path(),Path(),Path(),Path(),Path(),Path())

    var mFirePoint = PointF()
    var mSplitPoint = PointF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startFireMove()

        mSplitPoint = PointF(w / 2f,h / 4f)
        val point = mSplitPoint
        mSplitPaths.forEachIndexed { index, path ->
            val d = index % 4
            val rectF = RectF()
            var startAngle = 0f
            var sweepAngle = 0f
            var fx = 400
            var fy = 200

            when(d){
                0 ->{
                    sweepAngle = 90f
                    startAngle = if (index < 4){
                        rectF.set(point.x - fx,point.y - fy,point.x,point.y + fy)
                        270f
                    }else{
                        rectF.set(point.x,point.y - fy,point.x + fx,point.y + fy)
                        180f
                    }
                }
                1 ->{
                    sweepAngle = 180f - 30
                    fx /= 2
                    fy /= 2
                    startAngle = if (index < 4){
                        rectF.set(point.x - fx,point.y - fy,point.x,point.y + fy)
                        180f + 30f
                    }else{
                        rectF.set(point.x,point.y - fy,point.x + fx,point.y+ fy)
                        180f
                    }
                }
                2 ->{
                    sweepAngle = 90 - 30f
                    fx /= 2
                    startAngle = if (index < 4){
                        rectF.set(point.x - fx,point.y - 15,point.x + fx,point.y + 2 * fy)
                        180 + 30f
                    }else{
                        rectF.set(point.x - fx,point.y - 15,point.x + fx,point.y + 2 * fy)
                        270f
                    }
                }
                3 ->{
                    sweepAngle = 90f
                    fx /= 4
                    startAngle = if (index < 4){
                        rectF.set(point.x - fx,point.y,point.x + fx,point.y + 2 * fy)
                        180f
                    }else{
                        rectF.set(point.x - fx,point.y,point.x + fx,point.y + 2 * fy)
                        270f
                    }
                }
            }
            path.addArc(rectF,startAngle,sweepAngle)
        }
    }

    private fun startFireMove() {
        val h = height.toFloat()
        val valueAnimator = ValueAnimator.ofFloat(h, h / 4f).apply {
            duration = 1000
            repeatCount = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                mFirePoint.set(width / 2f, it.animatedValue as Float)
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startFireSplit()
                }
            })
            start()
        }
    }

    private fun startFireSplit() {
        val valueAnimator = ValueAnimator.ofFloat(0f,1f).apply {
            duration = 800
            repeatCount = -1
            addUpdateListener {
                mRadius = 10f + it.animatedFraction * 20f
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })
            start()
        }
    }

    var mRadius = 30f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mFirePoint.y > height / 4f) {
            val xOffset = 5
            val yOffset = 10
            canvas.drawOval(
                mFirePoint.x - xOffset,
                mFirePoint.y - yOffset,
                mFirePoint.x + xOffset,
                mFirePoint.y + yOffset,
                mPaint
            )
        }
            canvas.drawCircle(mSplitPoint.x,mSplitPoint.y,mRadius,mPaint)

        mSplitPaths.forEach {
            canvas.drawPath(it,mPaint2)
        }
    }

}