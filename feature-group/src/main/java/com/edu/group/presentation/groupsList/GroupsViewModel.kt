package com.edu.group.presentation.groupsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.data.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.usecase.GetAllGroups
import com.edu.group.domain.usecase.SaveUserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val getGroups: GetAllGroups,
    private val saveUserRole: SaveUserRole
) :
    BaseViewModel() {

    private val _allGroupsState: MutableLiveData<List<GroupDomain>> =
        MutableLiveData()
    val allGroupsState: LiveData<List<GroupDomain>>
        get() = _allGroupsState

    fun getAllGroups(query: String? = null) {
        showLoader()
        viewModelScope.launch {
            when (val result = getGroups(if (query.isNullOrEmpty()) null else query)) {
                is Result.Success -> {
                    _allGroupsState.value = result.data ?: emptyList()
                }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
            hideLoader()
        }
    }

    fun retrieveAndSaveUserRole() {
        viewModelScope.launch {
            when (val response = saveUserRole()) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }
}