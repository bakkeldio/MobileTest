package com.edu.mobiletest.ui.login

import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.ResourceState
import com.edu.mobiletest.R

import com.edu.mobiletest.databinding.FragmentLoginBinding
import com.edu.mobiletest.ui.flowFragments.SignInFlowFragmentDirections
import com.edu.mobiletest.utils.activityNavController
import com.edu.mobiletest.utils.isValidEmail
import com.edu.mobiletest.utils.removeErrorIfUserIsTyping
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>(
    R.layout.fragment_login,
    FragmentLoginBinding::bind
) {

    override val viewModel by hiltNavGraphViewModels<LoginViewModel>(R.id.sign_in_graph)

    private fun areLoginFieldsValid(): Boolean {
        var isValid = true
        if (!binding.emailEditText.text.toString().isValidEmail()) {
            isValid = false
            binding.emailField.error = resources.getString(R.string.enter_valid_email)
        }
        if (binding.passwordEditText.text.isNullOrEmpty()) {
            isValid = false
            binding.passwordField.error = resources.getString(R.string.empty_password)
        }
        return isValid
    }

    private fun attachListenersForFields() {
        binding.emailEditText.removeErrorIfUserIsTyping(binding.emailField)
        binding.passwordEditText.removeErrorIfUserIsTyping(binding.passwordField)
    }

    override fun setupUI() {
        attachListenersForFields()
        binding.loginBtn.setOnClickListener {
            if (areLoginFieldsValid()) {
                viewModel.signInUser(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        }
        binding.forgotPwd.setOnClickListener {
            findNavController().navigate(R.id.password_reset_fragment)
        }
    }

    override fun setupVM() {
        super.setupVM()
        lifecycleScope.launchWhenStarted {
            viewModel.signUserState.collect { state ->
                binding.progress.root.isVisible = state is ResourceState.Loading
                when (state) {
                    is ResourceState.Success -> {
                        activityNavController().navigate(SignInFlowFragmentDirections.actionSignInFragmentToMainFlowFragment())
                    }
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}