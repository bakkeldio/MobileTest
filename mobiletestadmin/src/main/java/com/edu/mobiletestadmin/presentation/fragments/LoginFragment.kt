package com.edu.mobiletestadmin.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.FragmentLoginBinding
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.viewModel.LoginViewModel
import com.edu.mobiletestadmin.utils.KeyboardTriggerBehavior
import com.edu.mobiletestadmin.utils.isValidEmail
import com.edu.mobiletestadmin.utils.showToast
import com.edu.mobiletestadmin.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {


    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val viewModel by viewModel<LoginViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            if (areLoginFieldsValid()) {
                viewModel.loginAdmin(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        }

        viewModel.loginLiveData.observe(viewLifecycleOwner) { state ->
            binding.progressLoader.root.isVisible = state is ResourceState.Loading
            binding.progressLoader.progressBar.isVisible = state is ResourceState.Loading
            when (state) {
                is ResourceState.Success -> {
                    findNavController().navigate(LoginFragmentDirections.actionFromLoginFragmentTo())
                }
                is ResourceState.Error -> {
                    showToast(state.error)
                }
                else -> Unit
            }
        }
        KeyboardTriggerBehavior(this).observe(viewLifecycleOwner) {
            if (it == KeyboardTriggerBehavior.Status.CLOSED){
                binding.root.requestFocus()
            }
        }
    }


    private fun areLoginFieldsValid(): Boolean {
        var isValid = true
        if (!binding.emailEditText.text.toString().isValidEmail()) {
            isValid = false
            binding.emailField.error = resources.getString(R.string.enter_valid_email)
        }
        if (binding.passwordEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.passwordField.error = resources.getString(R.string.enter_non_empty_password)
        }
        return isValid
    }


}