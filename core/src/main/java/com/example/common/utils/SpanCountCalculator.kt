package com.example.common.utils

import android.content.Context

object SpanCountCalculator {

    fun calculateNumOfColumns(context: Context, itemWidth: Float): Int {
        val metrics = context.resources.displayMetrics
        val dpWidth = metrics.widthPixels / metrics.density
        return (dpWidth / itemWidth).toInt()
    }
}