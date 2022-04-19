package com.edu.test.presentation.tests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.edu.common.data.Result
import com.edu.common.domain.model.TestDomainModel
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.PagedObject
import com.edu.common.presentation.mapper.TestDomainToUiMapperImpl
import com.edu.common.presentation.model.TestModel
import com.edu.common.presentation.model.TestStatusEnum
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TestsViewModel @Inject constructor(
    private val getGroupTests: GetAllTestsOfGroup,
    private val searchTests: SearchThroughGroupTests,
    private val searchCompletedTests: SearchThroughCompletedTests,
    private val completedTests: GetCompletedTests,
    private val getTestsOfTeacher: GetTestsOfTeacher,
    private val deleteTestUseCase: DeleteTestUseCase,
    private val workManager: WorkManager
) : BaseViewModel() {
    companion object {
        const val TESTS_LIST = 0
    }

    private val pagedObjects: HashMap<Int, PagedObject> = hashMapOf()

    val timerWorkResult: LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(TIMER_WORK)

    val uploadTestResult: LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(UPLOAD_WORK)


    init {
        pagedObjects[TESTS_LIST] = PagedObject()
    }


    private val _testsState: MutableLiveData<List<TestModel>> =
        MutableLiveData()

    val testsState: LiveData<List<TestModel>>
        get() = _testsState

    private val _completedTests: MutableLiveData<List<PassedTestDomain>> = MutableLiveData()
    val completedTestsState: LiveData<List<PassedTestDomain>> get() = _completedTests

    private val _deletedTest: MutableLiveData<Unit> = MutableLiveData()
    val deletedTest: LiveData<Unit> = _deletedTest

    fun getAllTests(groupId: String) {

        viewModelScope.launch {

            getGroupTests(groupId).collectLatest { result ->

                when (result) {
                    is TestsListState.Loading -> {
                        _loading.value = true
                    }
                    is TestsListState.Success -> {
                        _loading.value = false
                        getCompletedTests(groupId, result.data)
                    }
                    is TestsListState.Error -> {
                        _loading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    fun pruneWorks() {
        workManager.pruneWork()
    }

    fun updateTestsLiveData(tests: List<TestModel>) {
        _testsState.value = tests
    }

    private fun getCompletedTests(groupId: String, result: List<TestDomainModel>) {
        _loading.value = true
        viewModelScope.launch {
            completedTests(groupId).collect { state ->
                when (state) {
                    is Result.Success -> {
                        val completedTests = state.data ?: emptyList()
                        _completedTests.value = completedTests

                        val testsList = result.toMutableList()
                        val testId =
                            if (timerWorkResult.value?.size ?: 0 > 0 && timerWorkResult.value?.get(0)?.state == WorkInfo.State.RUNNING) {
                                timerWorkResult.value!![0].progress.getString("testId")
                            } else {
                                null
                            }

                        val testsUi = testsList.map { test ->
                            val isCompletedTest = completedTests.find { completedTest ->
                                completedTest.uid == test.uid
                            }
                            TestDomainToUiMapperImpl.mapToUi(test)
                                .copy(status = if (isCompletedTest != null) TestStatusEnum.PASSED else if (test.uid == testId) TestStatusEnum.IN_PROGRESS else TestStatusEnum.NOT_STARTED)
                        }
                        _testsState.value = testsUi
                    }
                    is Result.Error -> {
                        _error.value = state.data
                    }
                    else -> Unit
                }
                _loading.value = false
            }
        }
    }

    fun getTeacherTests(groupId: String) {
        viewModelScope.launch {
            getTestsOfTeacher(groupId).collectLatest { state ->
                when (state) {
                    is TestsListState.Loading -> {
                        _loading.value = true
                    }
                    is TestsListState.Success -> {
                        _loading.value = false
                        _testsState.value = state.data.map {
                            TestDomainToUiMapperImpl.mapToUi(it)
                        }
                    }
                    is TestsListState.Error -> {
                        _loading.value = false
                        _error.value = state.message
                    }
                }
            }
        }
    }

    fun searchThroughTests(
        groupId: String,
        query: String,
        isUserAdmin: Boolean
    ) {
        viewModelScope.launch {
            when (val result = searchTests(query, groupId, isUserAdmin)) {
                is Result.Success -> {
                    if (!isUserAdmin) {
                        getCompletedTests(groupId, result.data ?: emptyList())
                    } else {
                        _testsState.value = ((result.data) ?: emptyList()).map {
                            TestDomainToUiMapperImpl.mapToUi(it)
                        }
                    }
                }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
        }
    }

    fun searchThroughCompletedTests(
        groupId: String,
        query: String
    ) {
        viewModelScope.launch {
            when (val response = searchCompletedTests(groupId, query)) {
                is Result.Success -> {
                    _completedTests.value = response.data ?: emptyList()
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun deleteTest(groupId: String, testId: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = deleteTestUseCase(groupId, testId)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
            _loading.value = false
        }
    }

}