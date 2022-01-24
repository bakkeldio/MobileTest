package com.example.group.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.Result
import com.example.common.domain.group.model.CoreRoleEnum
import com.example.common.presentation.ResourceState
import com.example.group.domain.usecase.*
import com.example.group.presentation.model.GroupData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    val getGroupDetailsInfo: GetGroupDetails,
    val checkIfHasAccessToEnterGroup: CheckIfHasAccessToEnterGroup,
    val enterGroup: EnterGroup,
    val leaveGroup: LeaveGroup,
    val groupTests: GetGroupTests
) :
    ViewModel() {

    private val _groupDetailsState: MutableLiveData<ResourceState<GroupData>> = MutableLiveData()
    val groupDetailsState: LiveData<ResourceState<GroupData>>
        get() = _groupDetailsState

    private val _enterGroupState: MutableLiveData<ResourceState<Unit>> = MutableLiveData()

    val enterGroupState: LiveData<ResourceState<Unit>>
        get() = _enterGroupState

    private val _leaveGroupState: MutableLiveData<ResourceState<Unit>> = MutableLiveData()
    val leaveGroupState: LiveData<ResourceState<Unit>> get() = _leaveGroupState


    fun enterToGroup(groupId: String) {
        _enterGroupState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = enterGroup(groupId)) {
                is Result.Success -> {
                    fetchGroupDetails(groupId)
                    _enterGroupState.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _enterGroupState.value =
                        ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun leaveFromGroup(groupId: String) {
        _leaveGroupState.value = ResourceState.Loading
        viewModelScope.launch {
            delay(1000)
            when (val result = leaveGroup(groupId)) {
                is Result.Success -> {
                    fetchGroupDetails(groupId)
                    _leaveGroupState.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _leaveGroupState.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }


    fun fetchGroupDetails(groupId: String) {
        _groupDetailsState.value = ResourceState.Loading

        viewModelScope.launch {

            when (val result = getGroupDetailsInfo(groupId)) {
                is Result.Success -> {
                    val accessDeferred = async {
                        checkIfHasAccessToEnterGroup(groupId)
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
                                accessResult?.second,
                                role = accessResult?.first ?: CoreRoleEnum.STUDENT,
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
        }
    }

}