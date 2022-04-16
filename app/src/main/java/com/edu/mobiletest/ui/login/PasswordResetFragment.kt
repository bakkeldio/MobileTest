package com.edu.mobiletest.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.edu.common.presentation.ResourceState
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentPasswordResetBinding
import com.edu.mobiletest.utils.isValidEmail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordResetFragment : Fragment(){


    private val viewModel by activityViewModels<LoginViewModel>()
    private var _binding: FragmentPasswordResetBinding ?= null
    private val binding: FragmentPasswordResetBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())

        binding.sendBtn.setOnClickListener {
            if (binding.emailEditText.text.toString().isValidEmail()) {
                viewModel.sendPasswordResetEmail(binding.emailEditText.text.toString())
            }else{
                binding.emailField.error = resources.getString(R.string.enter_valid_email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.passwordResetState.collect { state ->
                binding.progress.root.isVisible = state is ResourceState.Loading
                when(state){
                    is ResourceState.Success -> {
                        Toast.makeText(requireContext(), resources.getString(R.string.email_reset_password_sent), Toast.LENGTH_LONG).show()
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
}