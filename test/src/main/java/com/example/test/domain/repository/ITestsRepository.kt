package com.example.test.domain.repository

import com.example.common.data.Result
import com.example.common.domain.model.TestDomainModel
import com.example.common.domain.model.TestResultDomain
import com.example.common.presentation.Pagination

interface ITestsRepository {

    suspend fun checkIfHasAccessToCreateTask(): Boolean

    suspend fun getAllTests(page: Int, groupId: String): Result<Pagination<TestDomainModel>>

    suspend fun getCurrentUserGroup(): Result<String>

    suspend fun searchTests(query: String, groupId: String): Result<List<TestDomainModel>>

    suspend fun submitTestResultOfUser(
        testId: String,
        groupId: String,
        testTitle: String
    ): Result<Unit>

    suspend fun getCompletedTests(groupId: String): Result<List<TestDomainModel>>

    suspend fun getCompletedTestQuestions(groupId: String, testId: String): Result<TestResultDomain>
}