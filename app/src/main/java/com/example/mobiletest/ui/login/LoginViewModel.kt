package com.example.mobiletest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiletest.data.IAuthRepository
import com.example.mobiletest.data.Result
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: IAuthRepository) :
    ViewModel() {

    val signUserState: MutableStateFlow<ResourceState<FirebaseUser?>> =
        MutableStateFlow(ResourceState.Empty)

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
}