package com.edu.mobiletestadmin.presentation.adapter

import androidx.recyclerview.selection.ItemKeyProvider

class ItemsKeyProvider(private val adapter: UsersAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {

    //2
    override fun getKey(position: Int): String =
        adapter.currentList[position].uid

    //3
    override fun getPosition(key: String): Int = adapter.currentList.indexOfFirst {
        it.uid == key
    }

}