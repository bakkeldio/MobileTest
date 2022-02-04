package com.example.test.utils

import android.os.Build
import android.view.View
import androidx.core.view.WindowInsetsCompat

object KeyboardManager {


    fun closeKeyboard(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (view.rootWindowInsets?.isVisible(WindowInsetsCompat.Type.ime()) == true) {
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
            }
        } else {
            view.clearFocus()
        }
    }
}