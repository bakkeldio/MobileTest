package com.example.test.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.model.TestDomainModel
import com.example.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SearchThroughGroupTests @Inject constructor(
    private val testsRepo: ITestsRepository
){
    suspend operator fun invoke(query: String, groupId: String): Result<List<TestDomainModel>>{
        return testsRepo.searchTests(query, groupId)
    }
}