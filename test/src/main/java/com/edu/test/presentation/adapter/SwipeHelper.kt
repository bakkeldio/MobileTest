package com.edu.test.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {
    companion object {
        private const val NO_POSITION = -1
        private const val START_POSITION = 0
        private val UNDERLAY_BUTTON_WIDTH_IN_PX: Int by lazy { 80 * Resources.getSystem().displayMetrics.density.toInt() }
    }

    private var buttons: List<UnderlayButton> = ArrayList()
    private var swipedPosition = NO_POSITION
    private var swipeThreshold = 0.5f

    private val buttonsBuffer: MutableMap<Int, List<UnderlayButton>> = mutableMapOf()
    private val recoverQueue = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            return if (contains(element)) {
                false
            } else {
                super.add(element)
            }
        }
    }


    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                for (button in buttons) {
                    if (e?.let { button.handle(it) } == true) break
                }
                return true
            }
        })

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        return@OnTouchListener if (swipedPosition < START_POSITION) {
            false
        } else {
            val point = Point(event.rawX.toInt(), event.rawY.toInt())

            val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPosition)
            val swipedItem = swipedViewHolder?.itemView
            val rect = Rect()
            swipedItem?.getGlobalVisibleRect(rect)

            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y) {
                    gestureDetector.onTouchEvent(event)
                } else {
                    recoverQueue.add(swipedPosition)
                    swipedPosition = NO_POSITION
                    recoverSwipedItem()
                }
            }
            false
        }
    }

    init {
        recyclerView.setOnTouchListener(touchListener)
    }

    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll() ?: return
            if (position > -1) recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        pos: Int
    ) {
        var right = itemView.right
        buttons.forEach { button ->
            val left = right - UNDERLAY_BUTTON_WIDTH_IN_PX
            button.onDraw(
                canvas,
                RectF(
                    left.toFloat(),
                    itemView.top.toFloat(),
                    right.toFloat(),
                    itemView.bottom.toFloat()
                ),
                pos
            )

            right = left
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val position = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView

        if (position < 0) {
            swipedPosition = position
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < START_POSITION) {
                if (!buttonsBuffer.containsKey(position)) {
                    buttonsBuffer[position] = instantiateUnderlayButton(position)
                }

                val buttons = buttonsBuffer[position] ?: return
                if (buttons.isEmpty()) return
                translationX = dX * buttons.size * UNDERLAY_BUTTON_WIDTH_IN_PX / itemView.width
                drawButtons(c, buttons, itemView, position)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (swipedPosition != position) recoverQueue.add(swipedPosition)
        swipedPosition = position

        if (buttonsBuffer.containsKey(swipedPosition)) {
            buttonsBuffer[swipedPosition]?.let { buttons = it }
        } else {
            buttons = emptyList()
        }

        buttonsBuffer.clear()
        swipeThreshold = 0.5F * buttons.size * UNDERLAY_BUTTON_WIDTH_IN_PX
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5F * defaultValue
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1F * defaultValue
    }

    abstract fun instantiateUnderlayButton(position: Int): List<UnderlayButton>

    inner class UnderlayButton(
        private val context: Context,
        private val imageResId: Int,
        @ColorRes private val color: Int,
        private val clickListener: (Int) -> Unit
    ) {
        private var pos: Int = 0
        private var clickableRegion: RectF? = null

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()
            p.color = ContextCompat.getColor(context, color)
            c.drawRect(
                rect,
                p,
            )

            val d = ContextCompat.getDrawable(context, imageResId)
            val bitmap = drawableToBitmap(d!!)

            val bw = (bitmap!!.width / 2).toFloat()
            val bh = (bitmap.height / 2).toFloat()
            c.drawBitmap(
                bitmap, (rect.left + rect.right) / 2 - bw,
                (rect.top + rect.bottom) / 2 - bh, p
            )

            clickableRegion = rect
            this.pos = pos
        }

        private fun drawableToBitmap(d: Drawable): Bitmap? {
            if (d is BitmapDrawable) {
                return Bitmap.createScaledBitmap(d.bitmap, 160, 160, true)
            }
            val bitmap =
                Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            d.setBounds(0, 0, canvas.width, canvas.height)
            d.draw(canvas)
            return bitmap
        }
        fun handle(event: MotionEvent): Boolean {
            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {
                    clickListener(pos)
                    return true
                }
            }
            return false
        }
    }
    //endregion
}

