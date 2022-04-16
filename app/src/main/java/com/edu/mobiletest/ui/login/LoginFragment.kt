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
import com.edu.common.presentation.ResourceState
import com.edu.mobiletest.MainActivity
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentLoginBinding
import com.edu.mobiletest.utils.isValidEmail
import com.edu.mobiletest.utils.removeErrorIfUserIsTyping
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel by activityViewModels<LoginViewModel>()
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfUserIsSignedIn()
        attachListenersForFields()
        binding.loginBtn.setOnClickListener {
            if (isLoginFieldsValid()) {
                viewModel.signInUser(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        }
        binding.forgotPwd.setOnClickListener {
            findNavController().navigate(R.id.password_reset_fragment)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.signUserState.collect { state ->
                binding.progress.root.isVisible = state is ResourceState.Loading
                when (state) {
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
        binding.registerBtn.setOnClickListener {
            findNavController().navigate(R.id.registration_fragment)
        }
    }

    private fun checkIfUserIsSignedIn() {
        if (auth.currentUser == null) {
            binding.group.isVisible = true
        } else {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun isLoginFieldsValid(): Boolean {
        var isValid = true
        if (!binding.emailEditText.text.toString().isValidEmail()) {
            isValid = false
            binding.emailField.error = resources.getString(R.string.enter_valid_email)
        }
        if (binding.passwordEditText.text.isNullOrEmpty()){
            isValid = false
            binding.passwordField.error = resources.getString(R.string.empty_password)
        }
        return isValid
    }

    private fun attachListenersForFields() {
        binding.emailEditText.removeErrorIfUserIsTyping(binding.emailField)
        binding.passwordEditText.removeErrorIfUserIsTyping(binding.passwordField)
    }
}