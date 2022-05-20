package com.edu.common.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.R

class DividerItemDecorator(context: Context, private val viewId: Int? = null) :
    RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider_item)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child = parent.getChildAt(i)
            val someView = viewId?.let {
                child.findViewById(it) as View
            }
            val spaceLeft = someView?.width ?: 0
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + mDivider.intrinsicHeight
            mDivider.setBounds(
                dividerLeft + spaceLeft + child.paddingLeft,
                dividerTop,
                dividerRight - child.paddingRight,
                dividerBottom
            )
            mDivider.draw(canvas)
        }
    }

}

