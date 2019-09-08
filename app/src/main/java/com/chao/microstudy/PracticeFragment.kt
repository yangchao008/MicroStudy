package com.chao.microstudy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_practice.*

/**
 * Date: 2019/8/12 16:35
 * Author: hans yang
 * Description:
 */
class PracticeFragment : Fragment(){

    private val mAdapter by lazy {
        MyAdapter().apply {

        }
    }
    private val texts = listOf(
        "DragCircularView",
        "WaterDropMoveView",
        "PathMeasureView",
        "SplitView"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_practice,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter.run {
            setNewData(texts)
            bindToRecyclerView(recyclerView)
            setOnItemClickListener { _, _, position ->
                val intent = Intent(activity, DragImageActivity::class.java)
                intent.putExtra(DetailActivity.VIEW_ID, position)
                startActivity(intent)
            }
        }

    }

    class MyAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_text_item) {

        override fun convert(helper: BaseViewHolder?, item: String?) {
            helper?.setText(R.id.text,item)
        }
    }
}