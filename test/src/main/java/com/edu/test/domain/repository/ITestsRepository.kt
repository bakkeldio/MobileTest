package com.edu.test.domain.repository

import com.edu.common.data.Result
import com.edu.common.domain.model.TestDomainModel
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.model.TestResultDomain
import com.edu.test.domain.model.TestsListState
import kotlinx.coroutines.flow.Flow

interface ITestsRepository {

    suspend fun checkIfHasAccessToCreateTask(): Boolean

    suspend fun getAllTests(groupId: String): Flow<TestsListState>

    suspend fun getCurrentUserGroup(): Result<String>

    suspend fun searchTests(
        query: String, groupId: String, isUserAdmin: Boolean
    ): Result<List<TestDomainModel>>

    suspend fun searchThroughCompletedTests(
        query: String, groupId: String
    ): Result<List<PassedTestDomain>>

    suspend fun submitTestResultOfUser(
        testId: String,
        groupId: String
    ): Result<Unit>

    fun getStudentsWhoPassedTheTest(
        testId: String
    ): Flow<Result<List<TestResultDomain>>>

    suspend fun getCompletedTests(groupId: String): Flow<Result<List<PassedTestDomain>>>

    suspend fun getCompletedTestQuestions(
        studentUid: String?,
        groupId: String,
        testId: String
    ): Flow<Result<TestResultDomain>>

    suspend fun createTest(groupId: String): Result<Unit>

    suspend fun getTestsOfTeacher(groupId: String): Flow<TestsListState>

    suspend fun deleteTest(groupId: String, testId: String): Result<Unit>

    suspend fun updateOpenQuestionScore(
        groupId: String,
        testId: String,
        questionId: String,
        studentUid: String,
        newScore: Int
    ): Result<Unit>

}