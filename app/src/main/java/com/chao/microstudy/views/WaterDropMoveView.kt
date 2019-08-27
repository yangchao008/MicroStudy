package com.chao.microstudy.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.chao.microstudy.R

/**
 * Date: 2019/8/13 15:11
 * Author: hans yang
 * Description:
 */
class WaterDropMoveView(context: Context) : View(context,null) {

    private val TAG = "WaterDropMoveView"

    private var mAPoint = PointF()
    private var mBPoint = PointF()
    private var mCPoint = PointF()
    private var mDPoint = PointF()

    private var mAWaterDrop = PointF()
    private var mBWaterDrop = PointF()
    private var mCWaterDrop = PointF()
    private var mDWaterDrop = PointF()

    private var mChangeWaterDrops = listOf(PointF(),PointF(),PointF(),PointF())

    private var mWaterPath = Path()
    private var mWaterDropPaths = listOf(Path(),Path(),Path(),Path())

    private val mRadius = 60f
    private var mChangeRadius = FloatArray(4)

    private val mPoints = listOf(mAPoint,mBPoint,mCPoint,mDPoint)
    private val mWaterDrops = listOf(mAWaterDrop,mBWaterDrop,mCWaterDrop,mDWaterDrop)
    private val texts = listOf("A","B","C","D")

    private val mTextBounds = Rect()

    private var mCircularPaint: Paint = Paint().apply{
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(getContext(), R.color.themeColor)
        style = Paint.Style.FILL
    }

    private var mTextPaint: Paint = Paint().apply{
        isAntiAlias = true
        color = ContextCompat.getColor(getContext(), R.color.white)
        textSize = 60f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val width = w / 5.toFloat()
        var height = h / 8.toFloat() * 7

        val heightOffset = 30
        mAPoint.set(width,height)
        mBPoint.set(width * 2,height - heightOffset)
        mCPoint.set(width * 3,height - heightOffset)
        mDPoint.set(width * 4,height)

        mWaterPath.moveTo(0.toFloat(),h.toFloat())
        mWaterPath.quadTo(w / 2.toFloat(), (h - 100).toFloat(),w.toFloat(),h.toFloat())

        val text = texts[0]
        mTextPaint.getTextBounds(text, 0, text.length, mTextBounds)

        height = h.toFloat()
        mAWaterDrop.set(width,height)
        mBWaterDrop.set(width * 2,height)
        mCWaterDrop.set(width * 3,height)
        mDWaterDrop.set(width * 4,height)

        for (i in mChangeRadius.size-1 downTo 0){
            mChangeRadius[i] = mRadius / 2
        }

        startWave()
        val delay = 500L
        startWaterPoint(0,delay)
        startWaterPoint(1,delay * 2)
        startWaterPoint(2,delay * 3)
        startWaterPoint(3,delay * 4)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawWaterDrop(canvas)
        drawStartState(canvas)
    }

    private fun drawWaterDrop(canvas: Canvas) {
        mWaterDrops.forEachIndexed { index, it ->
            val radius = mRadius / 2

            val changeRadius = mChangeRadius[index]

            canvas.drawCircle(it.x,it.y,radius,mCircularPaint)

            val point = mPoints[index]

//            if (it.y <= point.y){
//                return
//            }
            var offset = 30
            if (index == 2 || index == 1){
                offset+= 5
            }
            val centerY = (height + point.y) / 2
            val changeWaterDrops = mChangeWaterDrops[index]
            val waterDropPath = mWaterDropPaths[index]
            waterDropPath.reset()
            var controlOffset: Float
            if (it.y > centerY + offset) {
                controlOffset = (height - it.y) * 0.2f

                changeWaterDrops.set(it.x, height + changeRadius + radius + offset)

                waterDropPath.moveTo(changeWaterDrops.x - changeRadius,changeWaterDrops.y)
                val controlX = it.x
                val controlY = (changeWaterDrops.y + it.y) / 2.toFloat()
                waterDropPath.quadTo(controlX + controlOffset, controlY, it.x - radius, it.y)
                waterDropPath.lineTo(it.x + radius, it.y)
                waterDropPath.quadTo(controlX - controlOffset, controlY, changeWaterDrops.x + changeRadius, changeWaterDrops.y)
            }else
                if (it.y < centerY - offset + 10)
            {
                controlOffset = (it.y - point.y)* 0.05f

                changeWaterDrops.set(it.x,point.y)

                waterDropPath.moveTo(it.x - radius,it.y)
                val controlX = it.x
                val controlY = (changeWaterDrops.y + it.y) / 2.toFloat()
                waterDropPath.quadTo(controlX + controlOffset, controlY, changeWaterDrops.x - changeRadius, changeWaterDrops.y)
                waterDropPath.lineTo(changeWaterDrops.x + changeRadius, changeWaterDrops.y)
                waterDropPath.quadTo(controlX - controlOffset, controlY, it.x + radius, it.y)
            }
            canvas.drawCircle(changeWaterDrops.x,changeWaterDrops.y,changeRadius,mCircularPaint)
            canvas.drawPath(waterDropPath,mCircularPaint)
        }
    }

    private fun startWaterPoint(index: Int,delay: Long) {
        postDelayed({
            val point = mPoints[index]
            val movePoint = mWaterDrops[index]
            val animator = ValueAnimator.ofFloat(movePoint.y,point.y).apply {
                duration = 2000
//                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    movePoint.y = value
//                    val c = (movePoint.y + point.y) / 2
//                    mChangeRadius[index] = mRadius + c - value
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(p0: Animator?) {
                        upMovePoint(point,movePoint)
                    }
                })
                start()
            }
        },delay)
    }

    private fun upMovePoint(point: PointF,movePoint: PointF) {
        val offset = 15f
        ValueAnimator.ofFloat(0f, offset).apply {
            duration = 500
            addUpdateListener {
                var value = it.animatedValue as Float
                if (value > offset / 2){
                    value -= offset
                }
                point.y -= value
                movePoint.y -= value

                invalidate()
            }
            start()
        }
    }

    private fun startWave() {
        val animator = ValueAnimator.ofFloat(-1f,1f,-1f).apply {
            duration = 300
            repeatCount = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                Log.d(TAG,"startWave")
                val progress = it.animatedValue as Float
                mWaterPath.reset()
                val offset = 10 * progress
                mWaterPath.moveTo(0.toFloat() + offset,height.toFloat())
                val controlX = width / 2.toFloat()
                val controlY = height - 100 + offset
                mWaterPath.quadTo(controlX,controlY,width.toFloat() + offset,height.toFloat())
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                }
            })
            start()
        }
    }

    private fun drawStartState(canvas: Canvas) {
        mPoints.forEach {
            canvas.drawCircle(it.x,it.y,mRadius,mCircularPaint)
        }
        texts.forEachIndexed { index, text ->
            val center = mPoints[index]
            canvas.drawText(text,center.x - mTextBounds.centerX(),
                center.y - mTextBounds.centerY(),mTextPaint)
        }

        canvas.drawPath(mWaterPath,mCircularPaint)
    }

}