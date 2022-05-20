package com.edu.mobiletest.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.ResourceState
import com.edu.common.utils.KeyboardTriggerBehavior
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentPasswordResetBinding
import com.edu.mobiletest.utils.isValidEmail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordResetFragment : BaseFragment<LoginViewModel, FragmentPasswordResetBinding>(
    R.layout.fragment_password_reset,
    FragmentPasswordResetBinding::bind
) {

    override val viewModel by hiltNavGraphViewModels<LoginViewModel>(R.id.sign_in_graph)

    override fun setupVM() {
        super.setupVM()
        lifecycleScope.launchWhenStarted {
            viewModel.passwordResetState.collect { state ->
                binding.progress.root.isVisible = state is ResourceState.Loading
                when (state) {
                    is ResourceState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.email_reset_password_sent),
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().popBackStack()
                    }
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun setupUI() {
        binding.toolbar.setupWithNavController(findNavController())

        binding.sendBtn.setOnClickListener {
            if (binding.emailEditText.text.toString().isValidEmail()) {
                viewModel.sendPasswordResetEmail(binding.emailEditText.text.toString())
            } else {
                binding.emailField.error = resources.getString(R.string.enter_valid_email)
            }
        }
    }
}