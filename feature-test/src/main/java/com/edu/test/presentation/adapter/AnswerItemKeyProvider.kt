package com.edu.test.presentation.adapter

import androidx.recyclerview.selection.ItemKeyProvider

class AnswerItemKeyProvider : ItemKeyProvider<Long>(
    SCOPE_CACHED
) {
    override fun getKey(position: Int): Long {
        return position.toLong()
    }

    override fun getPosition(key: Long): Int {
        return key.toInt()
    }

}