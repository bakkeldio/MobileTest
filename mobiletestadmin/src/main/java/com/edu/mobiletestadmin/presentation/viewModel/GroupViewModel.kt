package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.data.repository.IGroupRepo
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepo: IGroupRepo) : BaseViewModel() {

    private val _groupInfo: MutableLiveData<ResourceState<UserGroup>> = MutableLiveData()
    val groupInfoLiveData: LiveData<ResourceState<UserGroup>> = _groupInfo

    private val _groupCreated: SingleLiveEvent<ResourceState<String>> = SingleLiveEvent()
    val groupCreatedLiveData: LiveData<ResourceState<String>> = _groupCreated

    private val _groupDeleted: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val groupDeleted: LiveData<Boolean> = _groupDeleted

    private val _groupStudents: MutableLiveData<ResourceState<List<User>>> = MutableLiveData()
    val groupStudents: LiveData<ResourceState<List<User>>> = _groupStudents

    private val _groupTeachers: MutableLiveData<ResourceState<List<User>>> = MutableLiveData()
    val groupTeachers: LiveData<ResourceState<List<User>>> = _groupTeachers

    private val _groupLogo: MutableLiveData<ResourceState<String?>> = MutableLiveData()
    val groupLogo: LiveData<ResourceState<String?>> = _groupLogo

    fun getGroupInfo(groupUid: String) {
        _groupInfo.value = ResourceState.Loading
        viewModelScope.launch {
            _groupInfo.value = toResourceState {
                groupRepo.getGroupInfo(groupUid)
            }
        }
    }

    fun createGroup(groupName: String, description: String, logoUri: String? = null) {
        viewModelScope.launch {
            _groupCreated.value = toResourceState {
                groupRepo.createGroup(groupName, description, logoUri)
            }
        }
    }

    fun deleteGroup(groupUid: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = groupRepo.deleteGroupUid(groupUid)) {
                is ResultFirebase.Success -> {
                    _groupDeleted.value = true
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun updateGroup(
        groupUid: String,
        groupName: String,
        description: String
    ) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = groupRepo.updateGroup(groupUid, groupName, description)) {
                is ResultFirebase.Success -> Unit
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun getGroupTeachers(groupUid: String) {
        _loading.value = true
        viewModelScope.launch {

            getResourceStateFlow {
                groupRepo.getGroupTeachers(groupUid)
            }.collect {
                _loading.value = false
                _groupTeachers.value = it
            }
        }
    }

    fun getGroupStudents(groupUid: String) {
        viewModelScope.launch {
            getResourceStateFlow {
                groupRepo.getGroupStudents(groupUid)
            }.collect {
                _loading.value = false
                _groupStudents.value = it
            }
        }
    }

    fun deleteGroupLogo(groupUid: String) {
        _groupLogo.value = ResourceState.Loading
        viewModelScope.launch {
            _groupLogo.value = toResourceState {
                groupRepo.deleteGroupLogo(groupUid)
            }
        }
    }

    fun updateGroupLogo(groupUid: String, logoUri: String) {
        _groupLogo.value = ResourceState.Loading
        viewModelScope.launch {
            _groupLogo.value = toResourceState {
                groupRepo.updateGroupLogo(groupUid, logoUri)
            }
        }
    }

    fun addUserToGroup(groupUid: String, studentUid: String, userType: UserTypeEnum) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = groupRepo.addUserToGroup(groupUid, studentUid, userType)) {
                is ResultFirebase.Success -> Unit
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun deleteUsersFromGroup(groupUid: String, usersIds: List<String>, userTypeEnum: UserTypeEnum) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = groupRepo.deleteUsersFromGroup(groupUid, usersIds, userTypeEnum)) {
                is ResultFirebase.Success -> Unit
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }


}