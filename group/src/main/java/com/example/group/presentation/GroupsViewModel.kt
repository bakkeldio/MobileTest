package com.example.group.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.Result
import com.example.group.domain.model.GroupDomain
import com.example.common.presentation.ResourceState
import com.example.group.domain.usecase.GetAllGroups
import com.example.group.domain.usecase.SaveUserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val getGroups: GetAllGroups,
    private val saveUserRole: SaveUserRole
) :
    ViewModel() {

    private val _allGroupsState: MutableLiveData<ResourceState<List<GroupDomain>>> =
        MutableLiveData()
    val allGroupsState: LiveData<ResourceState<List<GroupDomain>>>
        get() = _allGroupsState

    private val _saveUserRoleState: MutableLiveData<ResourceState<Unit>> = MutableLiveData()
    val saveUserRoleState: LiveData<ResourceState<Unit>> get() = _saveUserRoleState

    fun getAllGroups(query: String? = null) {
        _allGroupsState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = getGroups(query)) {
                is Result.Success -> {
                    _allGroupsState.value = ResourceState.Success(result.data ?: emptyList())
                }
                is Result.Error -> {
                    _allGroupsState.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun retrieveAndSaveUserRole() {
        viewModelScope.launch {
            when (val response = saveUserRole()){
                is Result.Success -> {
                    _saveUserRoleState.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _saveUserRoleState.value = ResourceState.Error(response.data?.localizedMessage ?: "")
                }
            }
        }
    }
}