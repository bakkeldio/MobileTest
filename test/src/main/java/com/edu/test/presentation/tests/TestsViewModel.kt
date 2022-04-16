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
import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.usecase.*
import com.edu.test.presentation.question.QuestionsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TestsViewModel @Inject constructor(
    private val getGroupTests: GetAllTestsOfGroup,
    private val searchTests: SearchThroughGroupTests,
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

    private val _completedTests: MutableLiveData<List<TestModel>> = MutableLiveData()
    val completedTestsState: LiveData<List<TestModel>> get() = _completedTests

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
                        getTestsStatuses(groupId, result.data)

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

    fun getCompletedTests(groupId: String) {
        _loading.value = true
        viewModelScope.launch {
            completedTests(groupId).collect { state ->
                when (state) {
                    is TestsListState.Success -> {
                        val completedTests = state.data
                        _completedTests.value = completedTests.map {
                            TestDomainToUiMapperImpl.mapToUi(it)
                        }
                    }
                    is TestsListState.Error -> {
                        _error.value = state.message
                    }
                    else -> Unit
                }
            }
            _loading.value = false
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
        isUserAdmin: Boolean,
        searchCompletedTests: Boolean
    ) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = searchTests(query, groupId, isUserAdmin, searchCompletedTests)) {
                is Result.Success -> {
                    if (!isUserAdmin && !searchCompletedTests) {
                        getTestsStatuses(groupId, result.data ?: emptyList())
                    } else {
                        if (searchCompletedTests) {
                            _completedTests.value = ((result.data) ?: emptyList()).map {
                                TestDomainToUiMapperImpl.mapToUi(it)
                            }
                        } else {
                            _testsState.value = ((result.data) ?: emptyList()).map {
                                TestDomainToUiMapperImpl.mapToUi(it)
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
            _loading.value = false
        }
    }

    fun deleteTest(groupId: String, testId: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = deleteTestUseCase(groupId, testId)) {
                is Result.Success -> { }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
            _loading.value = false
        }
    }

    private fun getTestsStatuses(groupId: String, result: List<TestDomainModel>) {
        viewModelScope.launch {
            val completedTestsDeferred = async {
                completedTests(groupId)
            }
            completedTestsDeferred.await().collectLatest {

                val completedTests =
                    (it as? TestsListState.Success)?.data ?: emptyList()
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

        }
    }
}