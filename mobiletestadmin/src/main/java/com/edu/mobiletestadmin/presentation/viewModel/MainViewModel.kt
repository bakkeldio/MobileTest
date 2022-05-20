package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.edu.mobiletestadmin.data.repository.IAuthRepo
import com.edu.mobiletestadmin.presentation.model.ResourceState
import kotlinx.coroutines.launch

class MainViewModel(private val authRepo: IAuthRepo) : BaseViewModel() {

    private val _adminLogoutState: SingleLiveEvent<ResourceState<Unit>> = SingleLiveEvent()

    val adminLogoutState: LiveData<ResourceState<Unit>> = _adminLogoutState

    fun isAdminSignedIn() = authRepo.isAdminSignedIn()


    fun signOut() {
        _adminLogoutState.value = ResourceState.Loading
        viewModelScope.launch {
            _adminLogoutState.value = toResourceState {
                authRepo.logout()
            }
        }
    }

}