package com.example.test.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.presentation.PagedObject
import com.example.common.presentation.Pagination
import com.example.common.presentation.ResourceState
import com.example.common.data.Result
import com.example.common.domain.test.model.TestDomainModel
import com.example.test.domain.usecase.GetAllTestsOfGroup
import com.example.test.domain.usecase.SearchThroughGroupTests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TestsViewModel @Inject constructor(
    private val getGroupTests: GetAllTestsOfGroup,
    private val searchTests: SearchThroughGroupTests,
) : ViewModel() {
    companion object {
        const val TESTS_LIST = 0
    }

    private val pagedObjects: HashMap<Int, PagedObject> = hashMapOf()

    init {
        pagedObjects[TESTS_LIST] = PagedObject()
    }

    private val _hasAccessState: MutableLiveData<ResourceState<Boolean>> =
        MutableLiveData()

    val hasAccessState: LiveData<ResourceState<Boolean>>
        get() = _hasAccessState

    private val _testsState: MutableLiveData<ResourceState<List<TestDomainModel>>> =
        MutableLiveData()

    val testsState: LiveData<ResourceState<List<TestDomainModel>>>
        get() = _testsState


    fun getAllTests(groupId: String) {
        if (pagedObjects[TESTS_LIST]?.hasNextPage == false) {
            return
        }
        val currentPage = pagedObjects[TESTS_LIST]?.currentPage ?: 1
        _testsState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val response = getGroupTests(currentPage, groupId)) {
                is Result.Success -> {
                    pagedObjects[TESTS_LIST]?.updatePage(response.data ?: Pagination())
                    _testsState.value = ResourceState.Success(response.data?.items ?: emptyList())
                }
                is Result.Error -> {
                    _testsState.value = ResourceState.Error(response.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun searchThroughTests(groupId: String, query: String){
        _testsState.value = ResourceState.Loading
        viewModelScope.launch {
            when(val result = searchTests(query, groupId)){
                is Result.Success -> {
                    if (result.data.isNullOrEmpty()){
                        _testsState.value = ResourceState.Empty
                    }else {
                        _testsState.value = ResourceState.Success(result.data!!)
                    }
                }
                is Result.Error -> {
                    _testsState.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }


}