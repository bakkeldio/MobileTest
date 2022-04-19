package com.edu.common.presentation

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.edu.common.utils.showToast
import com.edu.common.utils.viewBinding


abstract class BaseFragment<R : BaseViewModel, VB: ViewBinding>(@LayoutRes id: Int, bind: (View) -> VB) : Fragment(id) {


    protected abstract val viewModel: R

    protected val binding by viewBinding(bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupVM()
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

    abstract fun progressLoader(show: Boolean)
}