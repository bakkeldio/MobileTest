package com.edu.mobiletest.utils

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.edu.mobiletest.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    val pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{8,}" +                // at least 4 characters
                "$"
    )
    return pattern.matcher(this).matches()
}

fun TextInputEditText.removeErrorIfUserIsTyping(textInputLayout: TextInputLayout) {
    this.doOnTextChanged { _, _, _, _ ->
        textInputLayout.error = null
    }
}

fun Fragment.activityNavController(): NavController {
    return requireActivity().findNavController(R.id.nav_host_fragment_main)
}
