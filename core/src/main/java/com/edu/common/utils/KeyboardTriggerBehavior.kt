package com.edu.common.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class KeyboardTriggerBehavior(activity: Activity, private val minKeyboardHeight: Int = 0) : LiveData<Pair<KeyboardTriggerBehavior.Status, Int>>() {
    enum class Status {
        OPEN, CLOSED
    }

    private val contentView: View = activity.findViewById(android.R.id.content)

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val displayRect = Rect().apply { contentView.getWindowVisibleDisplayFrame(this) }
        val keypadHeight = contentView.rootView.height - displayRect.bottom
        if (keypadHeight > minKeyboardHeight) {
            setDistinctValue(Pair(Status.OPEN, keypadHeight))
        } else {
            setDistinctValue(Pair(Status.CLOSED, keypadHeight))
        }
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in Pair<Status, Int>>) {
        super.observe(owner, observer)
        observersUpdated()
    }

    override fun observeForever(observer: Observer<in Pair<Status, Int>>) {
        super.observeForever(observer)
        observersUpdated()
    }

    override fun removeObservers(owner: LifecycleOwner) {
        super.removeObservers(owner)
        observersUpdated()
    }

    override fun removeObserver(observer: Observer<in Pair<Status, Int>>) {
        super.removeObserver(observer)
        observersUpdated()
    }

    private fun setDistinctValue(newValue: Pair<Status, Int>) {
        if (value != newValue) {
            value = newValue
        }
    }

    private fun observersUpdated() {
        if (hasObservers()) {
            contentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        } else {
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
    }
}