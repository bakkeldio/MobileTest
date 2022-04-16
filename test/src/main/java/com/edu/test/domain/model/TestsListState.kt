package com.edu.test.domain.model

import com.edu.common.domain.model.TestDomainModel

sealed class TestsListState {
    object Loading : TestsListState()
    data class Success(val data: List<TestDomainModel>) : TestsListState()
    data class Error(val message: Throwable) : TestsListState()
}