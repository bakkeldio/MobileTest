package com.edu.test.presentation.createTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.data.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.test.domain.usecase.CreateTestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateTestViewModel @Inject constructor(private val createTest: CreateTestUseCase) :
    BaseViewModel() {

    private val _createTest: MutableLiveData<ResourceState<Unit>> = MutableLiveData()
    val createTestState: LiveData<ResourceState<Unit>> = _createTest

    fun createNewTest(groupId: String) {
        _createTest.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = createTest(groupId)) {
                is Result.Success -> {
                    _createTest.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _createTest.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

}