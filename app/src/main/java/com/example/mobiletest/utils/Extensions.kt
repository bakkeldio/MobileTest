package com.example.mobiletest.utils

import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


fun String.isValidEmail(): Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    val pattern = Pattern.compile("^" +
            "(?=.*[@#$%^&+=])" +     // at least 1 special character
            "(?=\\S+$)" +            // no white spaces
            ".{8,}" +                // at least 4 characters
            "$")
    return pattern.matcher(this).matches()
}

fun TextInputEditText.removeErrorIfUserIsTyping(textInputLayout: TextInputLayout){
    this.doOnTextChanged { _, _, _, _ ->
        textInputLayout.error = null
    }
}