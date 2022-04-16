package com.edu.test.domain.repository

import com.edu.common.data.Result
import com.edu.common.domain.model.TestDomainModel
import com.edu.common.domain.model.TestResultDomain
import com.edu.test.domain.model.TestsListState
import kotlinx.coroutines.flow.Flow

interface ITestsRepository {

    suspend fun checkIfHasAccessToCreateTask(): Boolean

    suspend fun getAllTests(groupId: String): Flow<TestsListState>

    suspend fun getCurrentUserGroup(): Result<String>

    suspend fun searchTests(
        query: String, groupId: String, isUserAdmin: Boolean, isSearchCompletedTests: Boolean,
    ): Result<List<TestDomainModel>>

    suspend fun submitTestResultOfUser(
        testId: String,
        groupId: String,
        testTitle: String
    ): Result<Unit>

    suspend fun getCompletedTests(groupId: String): Flow<TestsListState>

    suspend fun getCompletedTestQuestions(groupId: String, testId: String): Result<TestResultDomain>

    suspend fun createTest(groupId: String): Result<Unit>

    suspend fun getTestsOfTeacher(groupId: String): Flow<TestsListState>

    suspend fun deleteTest(groupId: String, testId: String): Result<Unit>
}