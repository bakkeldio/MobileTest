package com.edu.chat.presentation

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.edu.chat.databinding.ItemChatTimeHeaderBinding
import com.edu.chat.presentation.adapter.ChatMessagesAdapter

class StickyHeaderDecoration(private val adapter: ChatMessagesAdapter, root: View) :
    RecyclerView.ItemDecoration() {

    private val headerBinding by lazy { ItemChatTimeHeaderBinding.inflate(LayoutInflater.from(root.context)) }

    private val headerView: View
        get() = headerBinding.root

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val topChild = parent.getChildAt(0)
        val secondChild = parent.getChildAt(1)

        parent.getChildAdapterPosition(topChild)
            .let { topChildPosition ->

                if (adapter.currentList.isNotEmpty()) {
                    val header = adapter.currentList[topChildPosition]
                    //headerBinding.time.text = header.date
                }

                layoutHeaderView(topChild)

                canvas.drawHeaderView(topChild, secondChild)

            }
    }

    private fun layoutHeaderView(topView: View?) {
        topView?.let {
            headerView.measure(
                View.MeasureSpec.makeMeasureSpec(topView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )
            )
            headerView.layout(topView.left, 0, topView.right, headerView.measuredHeight)
        }
    }

    private fun Canvas.drawHeaderView(topView: View?, secondChild: View?) {
        save()
        translate(0f, calculateHeaderTop(topView, secondChild))
        headerView.draw(this)
        restore()
    }

    private fun calculateHeaderTop(topView: View?, secondChild: View?): Float =
        secondChild?.let { secondView ->
            val threshold = getPixels(headerBinding.root.context) + headerView.bottom
            if (secondView.findViewById<View>(headerView.id)?.visibility != View.GONE && secondView.top <= threshold) {
                (secondView.top - threshold).toFloat()
            } else {
                maxOf(topView?.top ?: 0, 0).toFloat()
            }
        } ?: maxOf(topView?.top ?: 0, 0).toFloat()

    private fun getPixels(context: Context): Int {
        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    fun hideHeader(){
        headerBinding.time.isVisible = false
    }

    fun showHeader(){
        headerBinding.time.isVisible = true
    }
}