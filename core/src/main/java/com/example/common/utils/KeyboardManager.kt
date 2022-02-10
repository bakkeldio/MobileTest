package com.example.common.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowInsetsCompat

object KeyboardManager {


    fun View.hideSoftKeyboard() {
        val windowToken = this.rootView?.windowToken
        windowToken?.let{
            val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it, 0)
        }
    }
}