package com.edu.test.presentation.adapter

import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolderForSelection(val view: View) : RecyclerView.ViewHolder(view) {


    abstract fun bind(position: Int)
    abstract fun getStringDetails(): ItemDetailsLookup.ItemDetails<String>
}