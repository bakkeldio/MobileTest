package com.edu.mobiletestadmin.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.util.regex.Pattern
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun RecyclerView.addDividerItemDecoration() {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        private val mDivider: Drawable =
            ContextCompat.getDrawable(context, R.drawable.divider_item)!!

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0..childCount - 2) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + mDivider.intrinsicHeight
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider.draw(c)
            }
        }

    })
}

inline fun <T : ViewBinding> Fragment.viewBinding(crossinline factory: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
            binding ?: factory(requireView()).also {
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                    viewLifecycleOwner.lifecycle.addObserver(this)
                    binding = it
                }
            }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }
    }

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    val pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=-])" +     // at least 1 special character
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


fun View.hideSoftKeyboard() {
    val windowToken = this.rootView?.windowToken
    windowToken?.let {
        val imm =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it, 0)
    }
    this.clearFocus()
}

fun Fragment.showToast(message: String?) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun SearchView.addQueryChangeListener(onDebouncingQueryTextChange: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        var debouncePeriod: Long = 500

        private val coroutineScope = CoroutineScope(Dispatchers.Main)
        var job: Job? = null
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            job?.cancel()
            job = coroutineScope.launch {
                newText?.let {
                    delay(debouncePeriod)
                    onDebouncingQueryTextChange(it)
                }
            }
            return false
        }

    })
}

fun Context.buildMaterialAlertDialog(
    description: String,
    @StringRes cancelBtnText: Int = R.string.cancel,
    @StringRes positiveBtnText: Int = R.string.agree,
    positiveBtnClick: () -> Unit,
    negativeBtnClick: (() -> Unit)? = null
): MaterialAlertDialogBuilder {

    return MaterialAlertDialogBuilder(this)
        .setMessage(description)
        .setNegativeButton(this.resources.getString(cancelBtnText)) { dialog, _ ->
            negativeBtnClick?.invoke()
            dialog.dismiss()
        }
        .setPositiveButton(this.resources.getString(positiveBtnText)) { dialog, _ ->
            positiveBtnClick()
            dialog.dismiss()
        }
}