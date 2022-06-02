package com.edu.test.presentation.testResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.test.domain.model.result.TestResultDomain
import com.edu.test.domain.usecase.GetStudentsWhoPassedTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestTakersViewModel @Inject constructor(
    private val getStudentsWhoPassedTest: GetStudentsWhoPassedTest
) : BaseViewModel() {


    private val _studentsWhoPassedTestLiveData: MutableLiveData<List<TestResultDomain>> =
        MutableLiveData()

    val studentsWhoPassedTestLiveData: LiveData<List<TestResultDomain>> =
        _studentsWhoPassedTestLiveData


    fun getStudents(testId: String) {
        _loading.value = true
        viewModelScope.launch {
            getStudentsWhoPassedTest(testId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _studentsWhoPassedTestLiveData.value = result.data
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
                _loading.value = false
            }
        }
    }
}