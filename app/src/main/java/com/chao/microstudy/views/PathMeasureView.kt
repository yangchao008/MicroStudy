package com.chao.microstudy.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.view.View
import com.chao.microstudy.R

class PathMeasureView(context: Context) : View(context) {

    private val mPaint = Paint()
    private val mLinePaint = Paint() //坐标系
    private val mBitmap: Bitmap

    private val mMatrix = Matrix()
    private val pos = FloatArray(2)
    private val tan = FloatArray(2)
    private val mPath = Path()
    private var mFloat: Float = 0.toFloat()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 4f

        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.color = Color.RED
        mLinePaint.strokeWidth = 6f

        //缩小图片
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_detail_page_favorite, options)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), mLinePaint)
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), mLinePaint)

        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())

        val path = Path()
        path.lineTo(0f, 200f)
        path.lineTo(200f, 200f)
        path.lineTo(200f, 0f)

//        /**
//         * pathMeasure需要关联一个创建好的path, forceClosed会影响Path的测量结果
//         */
//        val pathMeasure = PathMeasure()
//        pathMeasure.setPath(path, true)
//        Log.e("TAG", "onDraw:forceClosed=true " + pathMeasure.length)
//
//        val pathMeasure2 = PathMeasure()
//        pathMeasure2.setPath(path, false)
//        Log.e("TAG", "onDraw:forceClosed=false " + pathMeasure2.length)
//
//        val pathMeasure1 = PathMeasure(path, false)
//        Log.e("TAG", "onDraw:PathMeasure(path, false) " + pathMeasure1.length)
//
//        path.lineTo(200f, -200f)
//
//        Log.e("TAG", "onDraw:PathMeasure(path, false) " + pathMeasure1.length)
//        //如果Path进行了调整，需要重新调用setPath方法进行关联
//        pathMeasure1.setPath(path, false)
//
//        Log.e("TAG", "onDraw:PathMeasure(path, false) " + pathMeasure1.length)
//
//        val path = Path()
//        path.addRect(-200f, -200f, 200f, 200f, Path.Direction.CW)
//
//        val dst = Path()
//        dst.lineTo(-300f, -300f)//添加一条直线
//
//        val pathMeasure = PathMeasure(path, false)
//        //截取一部分存入dst中，并且使用moveTo保持截取得到的Path第一个点位置不变。
//        pathMeasure.getSegment(200f, 1000f, dst, true)
//
//        canvas.drawPath(path, mPaint)
//        canvas.drawPath(dst, mLinePaint)
//
//        val path = Path()
//        path.addRect(-100f, -100f, 100f, 100f, Path.Direction.CW)//添加一个矩形
//        path.addOval(-200f, -200f, 200f, 200f, Path.Direction.CW)//添加一个椭圆
//        canvas.drawPath(path, mPaint)
//        val pathMeasure = PathMeasure(path, false)
//        Log.e("TAG", "onDraw:forceClosed=false " + pathMeasure.length)
//        //跳转到下一条曲线
//        pathMeasure.nextContour()
//        Log.e("TAG", "onDraw:forceClosed=false " + pathMeasure.length)

        mPath.reset()
        mPath.addCircle(0f, 0f, 200f, Path.Direction.CW)
        canvas.drawPath(mPath, mPaint)

        mFloat += 0.005f
        if (mFloat >= 1) {
            mFloat = 0f
        }

//        val pathMeasure = PathMeasure(mPath, false)
//        pathMeasure.getPosTan(pathMeasure.length * mFloat, pos, tan)
//        Log.e("TAG", "onDraw: pos[0]=" + pos[0] + ";pos[1]=" + pos[1])
//        Log.e("TAG", "onDraw: tan[0]=" + tan[0] + ";tan[1]=" + tan[1])
//
//        //计算出当前的切线与x轴夹角的度数
//        val degrees = Math.atan2(tan[1].toDouble(), tan[0].toDouble()) * 180.0 / Math.PI
//        Log.e("TAG", "onDraw: degrees=$degrees")
//
//        mMatrix.reset()
//        //进行角度旋转
//        mMatrix.postRotate(degrees.toFloat(), (mBitmap.width / 2).toFloat(), (mBitmap.height / 2).toFloat())
//        //将图片的绘制点中心与当前点重合
//        mMatrix.postTranslate(pos[0] - mBitmap.width / 2, pos[1] - mBitmap.height / 2)
//        canvas.drawBitmap(mBitmap, mMatrix, mPaint)

        val pathMeasure = PathMeasure(mPath, false)
        //将pos信息和tan信息保存在mMatrix中
        pathMeasure.getMatrix(
            pathMeasure.length * mFloat,
            mMatrix,
            PathMeasure.POSITION_MATRIX_FLAG or PathMeasure.TANGENT_MATRIX_FLAG
        )
        //将图片的旋转坐标调整到图片中心位置
        mMatrix.preTranslate((-mBitmap.width / 2).toFloat(), (-mBitmap.height / 2).toFloat())

        canvas.drawBitmap(mBitmap, mMatrix, mPaint)

        invalidate()
    }


}
