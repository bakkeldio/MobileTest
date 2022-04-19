package com.edu.test.domain.usecase

import com.edu.common.data.Result
import com.edu.common.domain.model.TestDomainModel
import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SearchThroughGroupTests @Inject constructor(
    private val testsRepo: ITestsRepository
) {
    suspend operator fun invoke(
        query: String,
        groupId: String,
        isUserAdmin: Boolean
    ): Result<List<TestDomainModel>> {
        return testsRepo.searchTests(query, groupId, isUserAdmin)
    }
}