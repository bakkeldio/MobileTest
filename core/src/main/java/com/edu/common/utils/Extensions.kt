package com.edu.common.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edu.common.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun RecyclerView.addItemDecorationWithoutLastItem(@IdRes viewId: Int? = null) {
    if (layoutManager !is LinearLayoutManager)
        return
    addItemDecoration(DividerItemDecorator(context, viewId))
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

fun Fragment.createTempFileAndGetUri(): Uri {
    val file = File.createTempFile("temp_file", ".png", this.requireContext().cacheDir)
    return FileProvider.getUriForFile(
        requireContext(),
        "com.edu.mobiletest.provider",
        file
    )
}

fun Fragment.convertBitmapToUri(
    coroutineScope: CoroutineScope,
    fileName: String,
    bitmap: Bitmap
): Uri {
    coroutineScope.launch(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)
        context?.openFileOutput(fileName, Context.MODE_PRIVATE)?.use {
            it.write(stream.toByteArray())
        }
    }
    return Uri.fromFile(File(context?.filesDir?.absolutePath, fileName))
}

fun Fragment.createFileFromUri(uri: Uri): Uri {
    val fileName = uri.getOriginalFileName(requireContext()) ?: ""
    lifecycleScope.launch(Dispatchers.IO) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputFile =
            File(requireContext().filesDir, fileName)
        inputStream?.use { input ->
            val fileOutputStream = FileOutputStream(outputFile)
            fileOutputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        inputStream?.close()

    }
    return Uri.fromFile(File(context?.filesDir?.absolutePath, fileName))
}

fun String.getKeywords(): List<String> {
    val list = this.trim()
        .mapIndexedTo(mutableListOf()) { index, _ ->
            this.substring(0, index + 1)
        }
    list.add("")
    return list
}


fun Uri.getOriginalFileName(context: Context): String? {
    return context.contentResolver.query(this, null, null, null, null)?.use {
        val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameColumnIndex)
    }
}

fun File.getMimeTypeFromExtension(): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)
}

fun Fragment.openFile(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        addCategory(Intent.CATEGORY_DEFAULT)

        val uri = FileProvider.getUriForFile(requireContext(), "com.edu.mobiletest.provider", file)
        val mimeType = file.getMimeTypeFromExtension()
        mimeType?.let {
            setDataAndType(uri, it)
            startActivity(this)
        }

    }
}

fun TextView.setColorForText(@ColorRes id: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, id))
}

fun TextView.setBackground(@DrawableRes id: Int) {
    this.background = ContextCompat.getDrawable(this.context, id)
}

fun ImageView.setBackground(@DrawableRes id: Int) {
    this.background = ContextCompat.getDrawable(this.context, id)
}

fun Window.getSoftInputMode() = attributes.softInputMode

fun Int.convertToTimeRepresentation(): String {
    val hour = this % 86400 / 3600
    val minutes = this % 86400 % 3600 / 60
    val seconds = this % 86400 % 3600 % 60
    return "Осталось ${String.format("%02d:%02d:%02d", hour, minutes, seconds)}"
}

fun Date.getHourMinute(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.getDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.getDateAndTime(): String {
    val dayMonthFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
    val hourMinuteFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return "${dayMonthFormat.format(this)} ${hourMinuteFormat.format(this)}"
}

fun Date.isAfterCurrentDate() = this.after(Calendar.getInstance().time)












