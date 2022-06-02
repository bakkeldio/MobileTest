package com.edu.group.presentation.groupDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.group.domain.model.CoreRoleEnum
import com.edu.group.domain.usecase.CheckTheRoleOfTheUserInTheGroup
import com.edu.group.domain.usecase.GetGroupDetails
import com.edu.group.domain.usecase.GetGroupTests
import com.edu.group.domain.usecase.GetStudentsOfGroupUseCase
import com.edu.group.presentation.model.GroupData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    val getGroupDetailsInfo: GetGroupDetails,
    val checkTheRoleOfTheUserInTheGroup: CheckTheRoleOfTheUserInTheGroup,
    val groupTests: GetGroupTests,
    val getStudentsOfGroupUseCase: GetStudentsOfGroupUseCase
) :
    BaseViewModel() {

    private val _groupDetailsState: MutableLiveData<ResourceState<GroupData>> = MutableLiveData()
    val groupDetailsState: LiveData<ResourceState<GroupData>>
        get() = _groupDetailsState

    private val _students: MutableLiveData<List<StudentInfoDomain>> = MutableLiveData()
    val students: LiveData<List<StudentInfoDomain>> = _students


    fun getStudents(groupId: String) {
        viewModelScope.launch {
            getStudentsOfGroupUseCase(groupId).collectLatest {
                when (it) {
                    is Result.Success -> {
                        _students.value = it.data ?: emptyList()
                    }
                    is Result.Error -> {
                        _error.value = it.data
                    }
                }
            }
        }
    }

    fun fetchGroupDetails(groupId: String) {
        showLoader()

        viewModelScope.launch {

            when (val result = getGroupDetailsInfo(groupId)) {
                is Result.Success -> {
                    val accessDeferred = async {
                        checkTheRoleOfTheUserInTheGroup(groupId)
                    }

                    val groupTestsDeferred = async {
                        groupTests(groupId)
                    }

                    val accessResult = (accessDeferred.await() as? Result.Success)?.data
                    val groupTests = (groupTestsDeferred.await() as? Result.Success)?.data

                    _groupDetailsState.value = result.data?.let {
                        ResourceState.Success(
                            GroupData(
                                it,
                                role = accessResult ?: CoreRoleEnum.NONE,
                                tests = groupTests ?: emptyList()
                            )
                        )
                    } ?: ResourceState.Empty
                }
                is Result.Error -> {
                    _groupDetailsState.value =
                        ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
            hideLoader()
        }
    }

}