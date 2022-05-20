package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.data.repository.IAuthRepo
import com.edu.mobiletestadmin.presentation.model.ResourceState
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepo: IAuthRepo) : BaseViewModel() {

    private val _loginLiveData: SingleLiveEvent<ResourceState<Unit>> = SingleLiveEvent()
    val loginLiveData: LiveData<ResourceState<Unit>> = _loginLiveData


    fun loginAdmin(email: String, password: String) {
        _loginLiveData.value = ResourceState.Loading
        viewModelScope.launch {
            when (val response = authRepo.signInAdmin(email, password)) {
                is ResultFirebase.Success -> {
                    _loginLiveData.value = ResourceState.Success(response.data)
                }
                is ResultFirebase.Error -> {
                    _loginLiveData.value = ResourceState.Error(response.error.localizedMessage)
                }
            }
        }
    }
}