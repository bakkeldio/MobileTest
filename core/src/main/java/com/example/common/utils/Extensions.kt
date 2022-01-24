package com.example.common.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addItemDecorationWithoutLastItem(){
    if (layoutManager !is LinearLayoutManager)
        return
    addItemDecoration(DividerItemDecorator(context))
}

fun Fragment.showToast(message: String){
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

















