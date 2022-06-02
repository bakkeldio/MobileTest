package com.edu.mobiletest.ui.flowFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.mobiletest.domain.usecase.GetProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFlowViewModel @Inject constructor(private val getProfileImageUseCase: GetProfileImageUseCase) :
    BaseViewModel() {

    private val _imageUrl: MutableLiveData<String?> = MutableLiveData()
    val imageUrl: LiveData<String?> = _imageUrl


    fun getProfileImageUrl() {
        viewModelScope.launch {
            getProfileImageUseCase().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _imageUrl.value = result.data
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
            }
        }
    }

}