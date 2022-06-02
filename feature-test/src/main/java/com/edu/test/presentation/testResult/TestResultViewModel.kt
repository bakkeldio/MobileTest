package com.edu.test.presentation.testResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.test.domain.model.result.QuestionResultDomain
import com.edu.test.domain.usecase.CheckUserTestResult
import com.edu.test.domain.usecase.UpdateQuestionScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestResultViewModel @Inject constructor(
    private val checkUserTestResult: CheckUserTestResult,
    private val updateQuestionScoreUseCase: UpdateQuestionScoreUseCase
) : BaseViewModel() {

    val testResultError: MutableLiveData<String> = MutableLiveData()
    private val _testResult: MutableLiveData<List<QuestionResultDomain>> = MutableLiveData()
    val testResult: LiveData<List<QuestionResultDomain>> get() = _testResult


    fun getResultsOfTest(studentUid: String?, groupId: String, testId: String) {
        _loading.value = true
        viewModelScope.launch {
            checkUserTestResult(studentUid, groupId, testId).collect { result ->
                when (result) {

                    is Result.Success -> {
                        _testResult.value = result.data ?: emptyList()
                    }
                    is Result.Error -> {
                        testResultError.value = result.data?.localizedMessage ?: ""
                    }
                }
                _loading.value = false
            }
        }
    }

    fun updateQuestionScore(
        groupId: String,
        testId: String,
        questionId: String,
        studentUid: String,
        newScore: Int
    ) {
        _loading.value = true
        viewModelScope.launch {
            when (val result =
                updateQuestionScoreUseCase(groupId, testId, questionId, studentUid, newScore)) {
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