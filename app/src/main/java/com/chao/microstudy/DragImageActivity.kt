package com.chao.microstudy

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_drag_image.*
import android.view.WindowManager
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.os.Build
import android.view.View


class DragImageActivity : AppCompatActivity(){

    private val TAG = "DragImageActivity"

    var isTouchImgView = false

    private var mDragImgViewRect = Rect()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_image)


        resources.displayMetrics
        immersive()

        setHeightAndPadding(this,findViewById(R.id.toolbar))

    }

    private fun immersive() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色透明
            window.statusBarColor = Color.TRANSPARENT

            var visibility = window.decorView.systemUiVisibility
            //布局内容全屏展示
            visibility = visibility or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //隐藏虚拟导航栏
            visibility = visibility or SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //防止内容区域大小发生变化
            visibility = visibility or SYSTEM_UI_FLAG_LAYOUT_STABLE

            window.decorView.systemUiVisibility = visibility
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

    }

    private fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) {
            context.resources.getDimensionPixelSize(resId)
        } else 0
    }

    private fun setHeightAndPadding(context: Context, view: View) {
        val layoutParams = view.layoutParams
        layoutParams.height += getStatusBarHeight(context)
        view.setPadding(
            view.paddingLeft,
            view.paddingTop + getStatusBarHeight(context),
            view.paddingRight,
            view.paddingBottom
        )
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                isTouchImgView = isTouchImgView(x,y)

                return isTouchImgView
            }
            MotionEvent.ACTION_MOVE ->{
                if (isTouchImgView){
                    imgView.translationX = x - mDragImgViewRect.left.toFloat() - imgView.width / 2
                    imgView.translationY = y - mDragImgViewRect.top.toFloat() - imgView.height /2
                }
                return isTouchImgView
            }
            MotionEvent.ACTION_UP ->{
                isTouchImgView = false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isTouchImgView(x: Int, y: Int): Boolean {
        imgView.getGlobalVisibleRect(mDragImgViewRect)

        Log.d(TAG,"mDragImgViewRectF = $mDragImgViewRect--x = $x--y = $y")
        val contains = mDragImgViewRect.contains(x,y)

        if (contains){
            Toast.makeText(this,"触摸到自定义控件了",Toast.LENGTH_SHORT).show()
        }

        return contains
    }
}