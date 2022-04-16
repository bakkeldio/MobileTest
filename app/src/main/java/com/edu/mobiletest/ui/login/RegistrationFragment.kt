package com.edu.mobiletest.ui.login

import android.content.Intent
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
import androidx.navigation.ui.NavigationUI
import com.edu.common.presentation.ResourceState
import com.edu.mobiletest.MainActivity
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentRegistrationBinding
import com.edu.mobiletest.domain.model.NewUserData
import com.edu.mobiletest.utils.isValidEmail
import com.edu.mobiletest.utils.isValidPassword
import com.edu.mobiletest.utils.removeErrorIfUserIsTyping

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeErrors()
        binding.registerBtn.setOnClickListener {
            if (validateFields()) {
                viewModel.signUpUser(
                    NewUserData(
                        binding.firstNameText.text.toString(),
                        binding.lastNameText.text.toString(),
                        binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString()
                    )
                )
            }
        }
        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
        lifecycleScope.launchWhenStarted {
            viewModel.signUpUserState.collect {state ->
                binding.progress.root.isVisible = state is ResourceState.Loading
                when(state){
                    is ResourceState.Success -> {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun removeErrors() {
        binding.apply {
            firstNameText.removeErrorIfUserIsTyping(firstNameField)
            lastNameText.removeErrorIfUserIsTyping(lastNameField)
            emailEditText.removeErrorIfUserIsTyping(emailField)
            passwordEditText.removeErrorIfUserIsTyping(passwordField)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (binding.firstNameText.text.toString().isEmpty()){
            binding.firstNameField.error = resources.getString(R.string.empty_field)
            isValid = false
        }
        if (binding.lastNameText.text.toString().isEmpty()){
            binding.lastNameField.error = resources.getString(R.string.empty_field)
            isValid = false
        }
        if (!binding.emailEditText.text.toString().isValidEmail()) {
            binding.emailField.error = resources.getString(R.string.enter_valid_email)
            isValid = false
        }

        if (!binding.passwordEditText.text.toString().trim().isValidPassword()) {
            binding.passwordField.error = resources.getString(R.string.enter_valid_password)
            isValid = false
        }
        return isValid
    }

}