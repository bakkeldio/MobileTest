package com.example.mobiletest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.Result
import com.example.mobiletest.data.IAuthRepository
import com.example.mobiletest.data.IRolesRepository
import com.example.common.presentation.ResourceState
import com.example.mobiletest.data.UserRoleEnum
import com.example.mobiletest.ui.model.NewUserData
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val rolesRepository: IRolesRepository
) :
    ViewModel() {

    val signUserState: MutableStateFlow<ResourceState<FirebaseUser?>> =
        MutableStateFlow(ResourceState.Empty)

    val signUpUserState: MutableStateFlow<ResourceState<Unit>> =
        MutableStateFlow(ResourceState.Empty)

    val passwordResetState: MutableStateFlow<ResourceState<Unit>> = MutableStateFlow(
        ResourceState.Empty)

    fun signInUser(email: String, password: String) {
        signUserState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInUser(email, password)) {
                is Result.Success -> {
                    signUserState.emit(ResourceState.Success(result.data))
                }
                is Result.Error -> {
                    signUserState.emit(ResourceState.Error(result.data?.localizedMessage ?: ""))
                }
            }
        }
    }

    fun signUpUser(newUser: NewUserData, role: UserRoleEnum) {
        signUpUserState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signUpUser(newUser)) {
                is Result.Success -> {
                    val deferredUser = async {
                        updateUsersData(newUser)
                    }
                    val rolesData = async {
                        result.data?.uid?.let { rolesRepository.updateRoleForUser(it, role) }
                    }
                    deferredUser.await()
                    rolesData.await()
                }
                is Result.Error -> {
                    signUpUserState.emit(ResourceState.Error(result.data?.localizedMessage ?: ""))
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        passwordResetState.value = ResourceState.Loading
        viewModelScope.launch {
            when(val response = authRepository.sendPasswordResetEmail(email)){
                is Result.Success -> {
                    passwordResetState.emit(ResourceState.Success(Unit))
                }
                is Result.Error -> {
                    passwordResetState.emit(ResourceState.Error(response.data?.localizedMessage ?: ""))
                }
            }
        }
    }
    private fun updateUsersData(newUser: NewUserData) {
        viewModelScope.launch {
            when (val profileResult = authRepository.updateUsersData(newUser)) {
                is Result.Success -> {
                    signUpUserState.emit(ResourceState.Success(Unit))
                }
                is Result.Error -> {
                    signUpUserState.emit(
                        ResourceState.Error(
                            profileResult.data?.localizedMessage ?: ""
                        )
                    )
                }
            }
        }
    }
}