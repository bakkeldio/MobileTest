package com.example.test.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.common.data.Result
import com.example.common.presentation.BaseViewModel
import com.example.common.presentation.PagedObject
import com.example.common.presentation.Pagination
import com.example.common.presentation.mapper.TestDomainToUiMapperImpl
import com.example.common.presentation.model.TestModel
import com.example.common.presentation.model.TestStatusEnum
import com.example.test.domain.usecase.GetAllTestsOfGroup
import com.example.test.domain.usecase.GetCompletedTests
import com.example.test.domain.usecase.SearchThroughGroupTests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class TestsViewModel @Inject constructor(
    private val getGroupTests: GetAllTestsOfGroup,
    private val searchTests: SearchThroughGroupTests,
    private val completedTests: GetCompletedTests,
    private val workManager: WorkManager
) : BaseViewModel() {
    companion object {
        const val TESTS_LIST = 0
    }

    private val pagedObjects: HashMap<Int, PagedObject> = hashMapOf()

    val timerWorkResult: LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(QuestionsViewModel.TIMER_WORK)

    val uploadTestResult: LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(QuestionsViewModel.UPLOAD_WORK)


    init {
        pagedObjects[TESTS_LIST] = PagedObject()
    }

    val error: MutableLiveData<String> = MutableLiveData()

    private val _hasAccessState: MutableLiveData<Boolean> =
        MutableLiveData(false)

    val hasAccessState: LiveData<Boolean>
        get() = _hasAccessState

    private val _testsState: MutableLiveData<List<TestModel>> =
        MutableLiveData()

    val testsState: LiveData<List<TestModel>>
        get() = _testsState

    private val _completedTests: MutableLiveData<List<TestModel>> = MutableLiveData()
    val completedTestsState: LiveData<List<TestModel>> get() = _completedTests

    fun getAllTests(groupId: String) {
        if (pagedObjects[TESTS_LIST]?.hasNextPage == false) {
            return
        }
        val currentPage = pagedObjects[TESTS_LIST]?.currentPage ?: 1
        _loading.value = true
        viewModelScope.launch {
            when (val response = getGroupTests(currentPage, groupId)) {
                is Result.Success -> {
                    val completedTestsDeferred = async {
                        completedTests(groupId)
                    }
                    val completedTests =
                        (completedTestsDeferred.await() as? Result.Success)?.data ?: emptyList()
                    val testsList = response.data?.items?.toMutableList() ?: emptyList()
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
                    pagedObjects[TESTS_LIST]?.updatePage(response.data ?: Pagination())
                    _testsState.value = testsUi
                }
                is Result.Error -> {
                    error.value = response.data?.localizedMessage ?: ""
                }
            }
            _loading.value = false
        }
    }

    fun pruneWorks() {
        workManager.pruneWork()
    }

    fun updateTestsLiveData(tests: List<TestModel>){
        _testsState.value = tests
    }

    fun getCompletedTests(groupId: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = completedTests(groupId)) {
                is Result.Success -> {
                    val completedTests = response.data ?: emptyList()
                    _completedTests.value = completedTests.map {
                        TestDomainToUiMapperImpl.mapToUi(it)
                    }
                }
                is Result.Error -> {
                    error.value = response.data?.localizedMessage ?: ""
                }
            }
            _loading.value = false
        }
    }

    fun searchThroughTests(groupId: String, query: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = searchTests(query, groupId)) {
                is Result.Success -> {

                    _testsState.value = ((result.data) ?: emptyList()).map {
                        TestDomainToUiMapperImpl.mapToUi(it)
                    }
                }
                is Result.Error -> {
                    error.value = result.data?.localizedMessage ?: ""
                }
            }
            _loading.value = false
        }
    }

}