package com.chao.microstudy.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.chao.microstudy.R
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Date: 2019/8/12 17:38
 * Author: hans yang
 * Description:
 */
class DragCircularView(context: Context) : View(context,null) {

    private val TAG = "DragCircularView"
    private var mStatus = Status.DEFAULT_STATUS


    private var mCenter = PointF()
    private var mCircularCenter = PointF()
    private val mRadius = 40f
    private var mCircularRadius = mRadius
    private val mCircularBoundOffset = mRadius

    private var mSmallCircularRadius = mRadius

    private var mCircularPaint: Paint = Paint().apply{
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(getContext(), R.color.themeColor)
        style = Paint.Style.FILL
    }
    private var mSmallCircularPaint: Paint = Paint().apply{
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(getContext(), R.color.themeColor)
        style = Paint.Style.FILL
    }
    private var mTextPaint: Paint = Paint().apply{
        isAntiAlias = true
        color = ContextCompat.getColor(getContext(), R.color.white)
        textSize = 40f
    }

    private val text = "13"
    private val mTextBounds = Rect()

    private val mPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenter.set(width / 2.toFloat(), height / 2.toFloat())
        mCircularCenter.set(width / 2.toFloat(), height / 2.toFloat())

        mTextPaint.getTextBounds(text, 0, text.length, mTextBounds)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //状态1：画圆和文字
        //状态2：连接状态，画变化小圆和贝塞尔曲线
        //状态3：断开状态
        //状态4：放手回弹效果或者消失（根据离初始位置的距离）

        if (Status.CONNECT_STATUS == mStatus){
            drawSmallCircular(canvas)
        }

        if (Status.DISMISS_STATUS != mStatus){
            drawCircular(canvas)
        }

    }

    private fun drawSmallCircular(canvas: Canvas) {
        if (mSmallCircularRadius >= 0) {
            canvas.drawCircle(mCenter.x, mCenter.y, mSmallCircularRadius, mSmallCircularPaint)
            canvas.drawPath(mPath,mSmallCircularPaint)
        }
    }

    private fun drawCircular(canvas: Canvas) {
        canvas.drawCircle(mCircularCenter.x,mCircularCenter.y,mCircularRadius,mCircularPaint)

        canvas.drawText(text,mCircularCenter.x - mTextBounds.centerX(),
            mCircularCenter.y - mTextBounds.centerY(),mTextPaint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                if (Status.DISMISS_STATUS == mStatus){
                    mCircularRadius = mRadius
                    mCircularCenter = PointF(width / 2.toFloat(), height / 2.toFloat())
                    mStatus = Status.DEFAULT_STATUS
                }
                if (Status.DEFAULT_STATUS == mStatus) {
                    val x = event.x
                    val y = event.y
                    val distance = hypot(mCenter.x - x, mCenter.y - y)
                    Log.d(TAG,"distance = $distance")
                    if (distance < mRadius + mCircularBoundOffset) {
                        mStatus = Status.CONNECT_STATUS
                    }
                    return true
                }
            }
            MotionEvent.ACTION_MOVE ->{
                val x = event.x
                val y = event.y

                if (Status.CONNECT_STATUS == mStatus) {
                    mCircularCenter.x = x
                    mCircularCenter.y = y

                    val distance = hypot(mCenter.x - x, mCenter.y - y)
                    mSmallCircularRadius = mRadius - distance / 6
                    if (mSmallCircularRadius < 0f){
                        mStatus = Status.DISCONNECT_STATUS
                    }else {
                        var sinThe = abs(mCenter.y - mCircularCenter.y) /distance
                        var cosThe = abs(mCenter.x - mCircularCenter.x)/distance
                        Log.d(TAG,"mCenter.x = ${mCenter.x}；mCircularCenter.x = ${mCircularCenter.x}")
                        if ((mCenter.y > mCircularCenter.y && mCenter.x > mCircularCenter.x)
                            ||(mCenter.y < mCircularCenter.y && mCenter.x < mCircularCenter.x)){
                            sinThe = - sinThe
                        }

                        val aX = mCircularRadius * sinThe + mCircularCenter.x
                        val aY = mCircularRadius * cosThe + mCircularCenter.y
                        val bX = mCircularCenter.x - mCircularRadius * sinThe
                        val bY = mCircularCenter.y - mCircularRadius * cosThe
                        Log.d(TAG,"aX = $aX；bX = $bX")

                        val cX = mSmallCircularRadius * sinThe + mCenter.x
                        val cY = mSmallCircularRadius * cosThe + mCenter.y
                        val dX = mCenter.x - mSmallCircularRadius * sinThe
                        val dY = mCenter.y - mSmallCircularRadius * cosThe
                        Log.d(TAG,"cX = $cX；dX = $dX")

                        val controlX = (mCenter.x + mCircularCenter.x) / 2
                        val controlY = (mCenter.y + mCircularCenter.y) / 2
                        Log.d(TAG,"controlX = $controlX；controlY = $controlY")
                        mPath.reset()
                        mPath.moveTo(aX,aY)
                        mPath.quadTo(controlX,controlY,cX,cY)
                        mPath.lineTo(dX,dY)
                        mPath.quadTo(controlX,controlY,bX,bY)
                    }
                    invalidate()
                    return true
                }else if (Status.DISCONNECT_STATUS == mStatus) {
                    mCircularCenter.x = x
                    mCircularCenter.y = y
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP ->{
                if (Status.CONNECT_STATUS == mStatus) {
                    reset()
                }else if (Status.DISCONNECT_STATUS == mStatus) {
                    val distance = hypot(mCenter.x - event.x, mCenter.y - event.y)
                    if (distance < 10 * mRadius){
                        reset()
                    }else {
                        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                            duration = 800
                            addUpdateListener {
                                val progress = it.animatedValue as Float
                                mCircularRadius = mRadius * progress
                                invalidate()
                            }
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(p0: Animator?) {
                                    mStatus = Status.DISMISS_STATUS
                                }
                            })
                            start()
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun reset() {
        mPath.reset()
        mSmallCircularRadius = 0f
        val animator = ValueAnimator.ofObject(PointFEvaluator(),PointF(mCircularCenter.x,mCircularCenter.y),
                PointF(mCenter.x,mCenter.y)).apply {
            duration = 300
            interpolator = OvershootInterpolator(5f)

            addUpdateListener {
                val progress = it.animatedValue as PointF
                mCircularCenter = progress
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    mStatus = Status.DEFAULT_STATUS
                }
            })
            start()
        }
    }

    enum class Status {
        DEFAULT_STATUS,
        CONNECT_STATUS,
        DISCONNECT_STATUS,
        DISMISS_STATUS
    }
}