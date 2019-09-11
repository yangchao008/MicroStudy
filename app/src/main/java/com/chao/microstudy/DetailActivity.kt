package com.chao.microstudy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chao.microstudy.views.DragCircularView
import com.chao.microstudy.views.WaterDropMoveView
import com.chao.microstudy.views.PathMeasureView
import com.chao.microstudy.views.SplitView
import com.chao.microstudy.views.FireworksView

/**
 * Date: 2019/8/12 17:31
 * Author: hans yang
 * Description:
 */
class DetailActivity : AppCompatActivity(){

    companion object{
        const val VIEW_ID = "view_id"

        const val DragCircularView = 0
        const val WaterDropMoveView = 1
        const val PathMeasureView = 2
        const val SplitView = 3
        const val FireworksView = 4
        const val VIEW_ID_6 = 5
        const val VIEW_ID_7 = 6
        const val VIEW_ID_8 = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewId = intent?.getIntExtra(VIEW_ID,-1) ?: -1
        val view = getView(viewId)
        setContentView(view)

        title = getTitle(viewId)
    }

    private fun getTitle(viewId: Int):String {
        return when (viewId) {
            DragCircularView -> "DragCircularView"
            WaterDropMoveView -> "WaterDropMoveView"
            PathMeasureView -> "PathMeasureView"
            SplitView -> "SplitView"
            FireworksView -> "FireworksView"
            else ->"无标题"
        }
    }

    private fun getView(id: Int): View {

        return when(id){
            DragCircularView -> DragCircularView(this)
            WaterDropMoveView -> WaterDropMoveView(this)
            PathMeasureView -> PathMeasureView(this)
            SplitView -> SplitView(this)
            FireworksView -> FireworksView(this)
            else -> DragCircularView(this)
        }
    }
}