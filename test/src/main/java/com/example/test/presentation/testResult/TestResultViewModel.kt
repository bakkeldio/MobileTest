package com.example.test.presentation.testResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.data.Result
import com.example.common.presentation.BaseViewModel
import com.example.test.domain.model.QuestionResultDomain
import com.example.test.domain.usecase.CheckUserTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestResultViewModel @Inject constructor(
    private val checkUserTestResult: CheckUserTestResult
) : BaseViewModel() {

    val testResultError: MutableLiveData<String> = MutableLiveData()
    private val _testResult: MutableLiveData<List<QuestionResultDomain>> = MutableLiveData()
    val testResult: LiveData<List<QuestionResultDomain>> get() = _testResult


    fun getResultsOfTest(groupId: String, testId: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = checkUserTestResult(groupId, testId)) {
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