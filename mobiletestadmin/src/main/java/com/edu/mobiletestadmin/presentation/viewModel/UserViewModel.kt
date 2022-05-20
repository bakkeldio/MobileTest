package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.data.repository.IUsersRepo
import com.edu.mobiletestadmin.presentation.model.NewUser
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import kotlinx.coroutines.launch
import java.util.*

class UserViewModel(private val usersRepo: IUsersRepo) : BaseViewModel() {


    private val _userGroups: MutableLiveData<LinkedHashMap<String, UserGroup>> =
        MutableLiveData()
    val userGroups: LiveData<LinkedHashMap<String, UserGroup>> = _userGroups

    private val _newUserCreatedLiveData: SingleLiveEvent<ResourceState<Unit>> = SingleLiveEvent()
    val newUserCreateLiveData: LiveData<ResourceState<Unit>> = _newUserCreatedLiveData

    private val _userDetailsUpdatedLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val userDetailsUpdated: LiveData<Boolean> = _userDetailsUpdatedLiveData

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> = _email

    private val _userDeleted: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val userDeletedLiveData: LiveData<Boolean> = _userDeleted

    private val groupsForUser: LinkedHashMap<String, UserGroup> = linkedMapOf()

    fun setNewUserGroup(groups: UserGroup) {
        groupsForUser[groups.groupUid] = groups
        _userGroups.value = groupsForUser
    }

    fun removeGroups() {
        groupsForUser.clear()
        _userGroups.value = groupsForUser
    }

    fun removeFromGroup(groupUid: String) {
        groupsForUser.remove(groupUid)
        _userGroups.value = groupsForUser
    }

    fun generateRandomUIDPassword() = UUID.randomUUID().toString()

    fun createNewUser(
        email: String,
        password: String,
        name: String,
        surname: String,
        userTypeEnum: UserTypeEnum
    ) {
        val nameKeywords = generateNameKeywords(name, surname)
        _newUserCreatedLiveData.value = ResourceState.Loading

        viewModelScope.launch {
            when (val response = usersRepo.createNewUser(
                NewUser(
                    "$name $surname",
                    email,
                    password,
                    userTypeEnum,
                    userGroups.value?.values?.map {
                        it.groupUid
                    } ?: emptyList(),
                    nameKeywords
                )
            )) {
                is ResultFirebase.Success -> {
                    _newUserCreatedLiveData.value = ResourceState.Success(Unit)
                }
                is ResultFirebase.Error -> {
                    _newUserCreatedLiveData.value =
                        ResourceState.Error(response.error.localizedMessage)
                }
            }
        }
    }

    fun updateUserDetail(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        userTypeEnum: UserTypeEnum
    ) {
        val nameKeywords = generateNameKeywords(firstName, lastName)
        _loading.value = true
        viewModelScope.launch {
            when (val response = usersRepo.updateUserDetails(
                uid,
                NewUser(
                    "$firstName $lastName",
                    email,
                    password,
                    userTypeEnum,
                    userGroups.value?.values?.map {
                        it.groupUid
                    } ?: emptyList(),
                    nameKeywords
                )
            )) {
                is ResultFirebase.Success -> {
                    _userDetailsUpdatedLiveData.value = true
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun deleteUserByUid(uid: String, userTypeEnum: UserTypeEnum) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = usersRepo.deleteUser(uid, userTypeEnum)) {
                is ResultFirebase.Success -> {
                    _userDeleted.value = true
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun getUserEmailAndGroups(uid: String, ids: List<String>) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = usersRepo.getStudentEmail(uid)) {
                is ResultFirebase.Success -> {
                    _email.value = response.data
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            when (val response = usersRepo.getUserGroupsByIds(ids)) {
                is ResultFirebase.Success -> {
                    response.data.forEach {
                        groupsForUser[it.first] = UserGroup(it.first, it.second)
                    }
                    _userGroups.value = groupsForUser
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }


    private fun generateNameKeywords(firstName: String, lastName: String): List<String> {
        val nameWithFirstNameAtStart = "${firstName.trim()} ${lastName.trim()}"
        val nameWithLastNameAtStart = "${firstName.trim()} ${lastName.trim()}"

        val firstKeywords = nameWithFirstNameAtStart.mapIndexed { index, _ ->
            nameWithFirstNameAtStart.substring(0, index + 1)
        }
        val secondKeywords = nameWithLastNameAtStart.mapIndexed { index, _ ->
            nameWithLastNameAtStart.substring(0, index + 1)
        }
        val nameKeywords = mutableListOf<String>()
        nameKeywords.add(" ")
        nameKeywords.addAll(firstKeywords)
        nameKeywords.addAll(secondKeywords)
        return nameKeywords
    }

}