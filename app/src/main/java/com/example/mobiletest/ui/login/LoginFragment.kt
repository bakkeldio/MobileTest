package com.example.mobiletest.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobiletest.MainActivity
import com.example.mobiletest.R
import com.example.mobiletest.databinding.FragmentLoginBinding
import com.example.mobiletest.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<LoginViewModel>()
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
        binding.emailEditText.doOnTextChanged { _, _, _, _ ->
            binding.emailField.error = null
        }
        binding.passwordEditText.doOnTextChanged { _, _, _, _ ->
            binding.passwordField.error = null
        }
    }
}