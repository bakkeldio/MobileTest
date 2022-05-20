package com.edu.test.presentation.adapter

import androidx.recyclerview.selection.ItemKeyProvider

class TestItemKeyProvider(val adapter: TestsAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return adapter.currentList[position].uid
    }

    override fun getPosition(key: String): Int {
        return adapter.currentList.indexOfFirst {
            it.uid == key
        }
    }

}