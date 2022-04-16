package com.edu.mobiletest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.common.data.Result
import com.edu.common.presentation.ResourceState
import com.edu.mobiletest.data.IAuthRepository
import com.edu.mobiletest.domain.repository.IProfileRepository
import com.edu.mobiletest.domain.model.NewUserData
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val profileRepo: IProfileRepository
) :
    ViewModel() {

    val signUserState: MutableStateFlow<ResourceState<FirebaseUser?>> =
        MutableStateFlow(ResourceState.Empty)

    val signUpUserState: MutableStateFlow<ResourceState<Unit>> =
        MutableStateFlow(ResourceState.Empty)

    val passwordResetState: MutableStateFlow<ResourceState<Unit>> = MutableStateFlow(
        ResourceState.Empty
    )

    fun signInUser(email: String, password: String) {
        signUserState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInUser(email, password)) {
                is Result.Success -> {
                    (profileRepo.getCurrentRegistrationToken() as? Result.Success)?.data?.let { token ->
                        profileRepo.getFCMRegistrationTokens {
                            if (it.contains(token)) {
                                return@getFCMRegistrationTokens
                            }
                            it.add(token)
                            profileRepo.updateFCMRegistrationTokens(it)
                        }
                    }
                    signUserState.emit(ResourceState.Success(result.data))
                }
                is Result.Error -> {
                    signUserState.emit(ResourceState.Error(result.data?.localizedMessage ?: ""))
                }
            }
        }
    }

    fun signUpUser(newUser: NewUserData) {
        signUpUserState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signUpUser(newUser)) {
                is Result.Success -> { }
                is Result.Error -> {
                    signUpUserState.emit(ResourceState.Error(result.data?.localizedMessage ?: ""))
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        passwordResetState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val response = authRepository.sendPasswordResetEmail(email)) {
                is Result.Success -> {
                    passwordResetState.emit(ResourceState.Success(Unit))
                }
                is Result.Error -> {
                    passwordResetState.emit(
                        ResourceState.Error(
                            response.data?.localizedMessage ?: ""
                        )
                    )
                }
            }
        }
    }

}