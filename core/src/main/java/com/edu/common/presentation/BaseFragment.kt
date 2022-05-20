package com.edu.common.presentation

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.edu.common.utils.KeyboardTriggerBehavior
import com.edu.common.utils.getSoftInputMode
import com.edu.common.utils.showToast
import com.edu.common.utils.viewBinding


abstract class BaseFragment<R : BaseViewModel, VB : ViewBinding>(
    @LayoutRes id: Int,
    bind: (View) -> VB
) : Fragment(id) {


    protected abstract val viewModel: R

    protected val binding by viewBinding(bind)

    private var softInputMode: Int? = null

    protected fun setAdjustResizeSoftInput() {
        softInputMode = requireActivity().window.getSoftInputMode()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupVM()

        KeyboardTriggerBehavior(this).observe(viewLifecycleOwner) { status ->
            if (status == KeyboardTriggerBehavior.Status.CLOSED)
                requireActivity().currentFocus?.clearFocus()
        }
    }

    abstract fun setupUI()
    open fun setupVM() {
        viewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            progressLoader(it)
        }
    }

    open fun progressLoader(show: Boolean) {}

    override fun onDestroyView() {
        super.onDestroyView()
        softInputMode?.let {
            requireActivity().window.setSoftInputMode(it)
        }
    }
}