package com.edu.common.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edu.common.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

fun RecyclerView.addItemDecorationWithoutLastItem() {
    if (layoutManager !is LinearLayoutManager)
        return
    addItemDecoration(DividerItemDecorator(context))
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.buildMaterialAlertDialog(
    description: String,
    @StringRes cancelBtnText: Int = R.string.cancel,
    @StringRes positiveBtnText: Int = R.string.agree,
    positiveBtnClick: () -> Unit,
    negativeBtnClick: (() -> Unit)? = null
): MaterialAlertDialogBuilder {

    return MaterialAlertDialogBuilder(requireContext())
        .setMessage(description)
        .setNegativeButton(requireContext().resources.getString(cancelBtnText)) { dialog, _ ->
            negativeBtnClick?.invoke()
            dialog.dismiss()
        }
        .setPositiveButton(requireContext().resources.getString(positiveBtnText)) { dialog, _ ->
            positiveBtnClick()
            dialog.dismiss()
        }
}

fun String.setForegroundColorSpan(@ColorRes color: Int, context: Context): SpannableString {
    val spannable = SpannableString(this)
    spannable.setSpan(
        ForegroundColorSpan(context.resources.getColor(color)),
        0,
        this.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}


fun Date.parseDateForChat(): String {
    val thisDateCalendar = Calendar.getInstance()
    val currentCalendar = Calendar.getInstance()
    val anotherCalendar = Calendar.getInstance()
    anotherCalendar.add(Calendar.DAY_OF_MONTH, -7)
    thisDateCalendar.time = this

    return when {
        thisDateCalendar[Calendar.YEAR] != currentCalendar[Calendar.YEAR] -> {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this)
        }
        thisDateCalendar[Calendar.MONTH] != currentCalendar[Calendar.MONTH] -> SimpleDateFormat(
            "EE, d MMMM",
            Locale.getDefault()
        ).format(this)
        thisDateCalendar.time in anotherCalendar.time..currentCalendar.time -> {
            when {
                thisDateCalendar[Calendar.DAY_OF_MONTH] + 1 == currentCalendar[Calendar.DAY_OF_MONTH] -> "Вчера"
                thisDateCalendar[Calendar.DAY_OF_MONTH] == currentCalendar[Calendar.DAY_OF_MONTH] -> "Сегодня"
                else -> SimpleDateFormat("EEEE", Locale.getDefault()).format(this)
            }
        }
        else -> {
            SimpleDateFormat("EE, d MMMM", Locale.getDefault()).format(this)
        }
    }

}

fun Date.getTimeFromDate(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
}

fun TextView.rightDrawable(@DrawableRes id: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0)
}

fun TextView.leftDrawable(@DrawableRes id: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0)
}

fun TextView.removeAnyDrawables() {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun View.hideSoftKeyboard() {
    val windowToken = this.rootView?.windowToken
    windowToken?.let {
        val imm =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it, 0)
    }
    this.clearFocus()
}

fun ViewPager2.removeOverScroll() {
    (getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
}
















