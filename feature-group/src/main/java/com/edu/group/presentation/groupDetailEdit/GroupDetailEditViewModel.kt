package com.edu.group.presentation.groupDetailEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailEditViewModel @Inject constructor(
    val getStudentsOfGroupUseCase: GetStudentsOfGroupUseCase,
    val deleteStudentsUseCase: DeleteStudentFromGroupUseCase,
    val getGroupDetailsInfo: GetGroupDetails,
    val getStudentsToAddUseCase: GetStudentsToAddUseCase,
    val addStudentToGroupUseCase: AddStudentToGroupUseCase,
    val updateGroupDetailsUseCase: UpdateGroupDetailsUseCase,
    val uploadGroupAvatar: UploadGroupAvatarToStorageUseCase,
    val updateGroupAvatarInDatabase: UpdateGroupAvatarInDBUseCase
) : BaseViewModel() {

    private val _studentsList: MutableLiveData<List<StudentInfoDomain>> = MutableLiveData()
    val studentsList: LiveData<List<StudentInfoDomain>> = _studentsList

    private val _groupInfo: MutableLiveData<GroupDomain> = MutableLiveData()
    val groupInfo: LiveData<GroupDomain> = _groupInfo

    private val _studentsToAddList: MutableLiveData<ResourceState<List<StudentInfoDomain>>> =
        MutableLiveData()
    val studentsToAddList: LiveData<ResourceState<List<StudentInfoDomain>>> = _studentsToAddList

    private val _studentAdd: MutableLiveData<ResourceState<Unit>> = MutableLiveData()
    val studentAdd: LiveData<ResourceState<Unit>> = _studentAdd

    private val _groupAvatar: MutableLiveData<String> = MutableLiveData()
    val groupAvatar: LiveData<String> = _groupAvatar

    private val _groupDetailsUpdate: MutableLiveData<Boolean> = MutableLiveData()
    val groupDetailsUpdated: LiveData<Boolean> = _groupDetailsUpdate

    private fun getStudentsOfGroup(groupId: String) {
        viewModelScope.launch {
            getStudentsOfGroupUseCase(groupId).collectLatest { response ->
                when (response) {
                    is Result.Success -> {
                        _studentsList.value = response.data ?: emptyList()
                    }
                    is Result.Error -> {
                        _error.value = response.data
                    }
                }
            }
        }
    }

    fun deleteStudentFromGroup(groupId: String, studentId: String) {
        showLoader()
        viewModelScope.launch {
            when (val response = deleteStudentsUseCase(groupId, studentId)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }

    fun getGroupDetails(groupId: String) {
        showLoader()
        viewModelScope.launch {
            getStudentsOfGroup(groupId)
            when (val response = getGroupDetailsInfo(groupId)) {
                is Result.Success -> {
                    _groupInfo.value = response.data
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }

    fun getStudentsToAdd() {
        _studentsToAddList.value = ResourceState.Loading
        viewModelScope.launch {
            getStudentsToAddUseCase().collectLatest {
                when (it) {
                    is Result.Success -> {
                        val students = it.data ?: emptyList()
                        if (students.isEmpty()) {
                            _studentsToAddList.value = ResourceState.Empty
                        } else {
                            _studentsToAddList.value = ResourceState.Success(students)
                        }
                    }
                    is Result.Error -> {
                        _studentsToAddList.value = ResourceState.Error("")
                        _error.value = it.data
                    }
                }
            }
        }
    }

    fun addStudent(groupId: String, student: StudentInfoDomain) {
        _studentAdd.value = ResourceState.Loading
        viewModelScope.launch {
            when (val response = addStudentToGroupUseCase(groupId, student)) {
                is Result.Success -> {
                    _studentAdd.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _studentAdd.value = ResourceState.Error("")
                    _error.value = response.data
                }
            }
        }
    }

    fun changeGroupDetails(groupDomain: GroupDomain) {
        showLoader()
        viewModelScope.launch {
            when (val response = updateGroupDetailsUseCase(groupDomain)) {
                is Result.Success -> {
                    _groupDetailsUpdate.value = true
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }

    private fun updateGroupAvatarInDB(groupId: String, downloadUrl: String) {
        viewModelScope.launch {
            when (val response = updateGroupAvatarInDatabase(groupId, downloadUrl)) {
                is Result.Success -> { }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun updateGroupAvatar(groupId: String, uri: String) {
        showLoader()
        viewModelScope.launch {
            when (val response = uploadGroupAvatar(groupId, uri)) {
                is Result.Success -> {
                    _groupAvatar.value = response.data
                    updateGroupAvatarInDB(groupId, response.data ?: "")
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }


}