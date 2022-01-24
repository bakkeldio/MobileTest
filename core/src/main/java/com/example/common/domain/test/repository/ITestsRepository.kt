package com.example.common.domain.test.repository

import com.example.common.presentation.Pagination
import com.example.common.data.Result
import com.example.common.domain.test.model.TestDomainModel

interface ITestsRepository {

    suspend fun checkIfHasAccessToCreateTask(): Boolean

    suspend fun getAllTests(page: Int, groupId: String): Result<Pagination<TestDomainModel>>

    suspend fun getCurrentUserGroup(): Result<String>

    suspend fun searchTests(query: String, groupId: String): Result<List<TestDomainModel>>
}