package com.chao.microstudy.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.chao.microstudy.R

class MyImgView (context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private val TAG = "MyImgView"

    var mBitmap: Bitmap

    var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
        mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_directed_avator)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mBitmap.width,MeasureSpec.EXACTLY)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mBitmap.height,MeasureSpec.EXACTLY)

        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG,"width = ${mBitmap.width}")
        canvas.drawBitmap(mBitmap,0f,0f,mPaint)
    }
}