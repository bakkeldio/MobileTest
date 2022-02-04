package com.example.common.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun RecyclerView.addItemDecorationWithoutLastItem() {
    if (layoutManager !is LinearLayoutManager)
        return
    addItemDecoration(DividerItemDecorator(context))
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.buildMaterialAlertDialog(
    title: String,
    @StringRes cancelBtnText: Int = R.string.cancel,
    @StringRes positiveBtnText: Int = R.string.agree,
    positiveBtnClick: () -> Unit,
    negativeBtnClick: (() -> Unit)? = null
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(requireContext())
        .setMessage(title)
        .setNegativeButton(requireContext().resources.getString(cancelBtnText)) { dialog, _ ->
            negativeBtnClick?.invoke()
            dialog.dismiss()
        }
        .setPositiveButton(requireContext().resources.getString(positiveBtnText)) { dialog, _ ->
            positiveBtnClick()
            dialog.dismiss()
        }
}

fun String.setForegroundColorSpan(@ColorRes color: Int, context: Context): SpannableString{
    val spannable = SpannableString(this)
    spannable.setSpan(ForegroundColorSpan(context.resources.getColor(color)), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannable
}
















