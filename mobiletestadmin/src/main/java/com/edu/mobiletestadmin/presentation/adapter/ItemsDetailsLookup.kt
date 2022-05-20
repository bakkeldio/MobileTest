package com.edu.mobiletestadmin.presentation.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {

    //2
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        //3
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            //4
            return (recyclerView.getChildViewHolder(view) as UsersAdapter.UserVH).getItem()
        }
        return null
    }
}