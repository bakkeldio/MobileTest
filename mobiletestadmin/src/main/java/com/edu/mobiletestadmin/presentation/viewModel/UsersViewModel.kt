package com.edu.mobiletestadmin.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.data.repository.IUsersRepo
import com.edu.mobiletestadmin.presentation.model.ResourceState
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UsersViewModel(private val usersRepo: IUsersRepo) : BaseViewModel() {


    private val _studentsLiveData: SingleLiveEvent<ResourceState<List<User>>> = SingleLiveEvent()
    val studentsLiveData: LiveData<ResourceState<List<User>>>
        get() = _studentsLiveData

    private val _teachersLiveData: SingleLiveEvent<ResourceState<List<User>>> = SingleLiveEvent()
    val teachersLiveData: LiveData<ResourceState<List<User>>>
        get() = _teachersLiveData


    fun getStudents(studentWithoutGroup: Boolean = false) {
        _studentsLiveData.value = ResourceState.Loading
        viewModelScope.launch {
            usersRepo.getStudentsList().catch {
                emit(ResultFirebase.Error(it))
            }.collectLatest { result ->
                when (result) {
                    is ResultFirebase.Success -> {
                        val students = if (studentWithoutGroup) {
                            result.data.filter {
                                it.groupId.isEmpty()
                            }
                        } else {
                            result.data
                        }
                        _studentsLiveData.value =
                            if (students.isEmpty()) ResourceState.Empty else ResourceState.Success(
                                result.data
                            )
                    }
                    is ResultFirebase.Error -> {
                        _studentsLiveData.value = ResourceState.Error(result.error.localizedMessage)
                    }
                }
            }
        }
    }

    fun getTeachers() {
        _teachersLiveData.value = ResourceState.Loading
        viewModelScope.launch {
            usersRepo.getTeachersList().catch {
                emit(ResultFirebase.Error(it))
            }.collectLatest { result ->
                when (result) {
                    is ResultFirebase.Success -> {
                        _teachersLiveData.value =
                            if (result.data.isEmpty()) ResourceState.Empty else
                                ResourceState.Success(result.data)
                    }

                    is ResultFirebase.Error -> {
                        _teachersLiveData.value = ResourceState.Error(result.error.localizedMessage)
                    }
                }
            }
        }
    }

    fun searchUsers(query: String, userTypeEnum: UserTypeEnum) {
        viewModelScope.launch {
            when (val response = usersRepo.searchUsers(query, userTypeEnum)) {
                is ResultFirebase.Success -> {
                    when (userTypeEnum) {
                        UserTypeEnum.TEACHER -> {
                            _teachersLiveData.value = ResourceState.Success(response.data)
                        }
                        UserTypeEnum.STUDENT -> {
                            _studentsLiveData.value = ResourceState.Success(response.data)
                        }
                    }
                }
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
        }
    }

    fun deleteStudents(uids: List<String>) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = usersRepo.deleteStudents(uids)) {
                is ResultFirebase.Success -> Unit
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

    fun deleteTeachers(uids: List<String>) {
        _loading.value = true

        viewModelScope.launch {
            when (val response = usersRepo.deleteTeachers(uids)) {
                is ResultFirebase.Success -> Unit
                is ResultFirebase.Error -> {
                    _error.value = response.error
                }
            }
            _loading.value = false
        }
    }

}