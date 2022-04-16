package com.edu.group.presentation.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.data.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.presentation.BaseViewModel
import com.edu.group.domain.usecase.GetStudentsWithRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupStudentsRatingViewModel @Inject constructor(private val getRatingOfStudents: GetStudentsWithRatingUseCase) :
    BaseViewModel() {

    private val _studentsWithRating: MutableLiveData<List<StudentInfoDomain>> = MutableLiveData()

    val studentsWithRating: LiveData<List<StudentInfoDomain>> = _studentsWithRating


    fun getStudentsWithRating(groupId: String) {
        showLoader()
        viewModelScope.launch {
            getRatingOfStudents(groupId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _studentsWithRating.value = result.data ?: emptyList()
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
                hideLoader()
            }
        }
    }

}