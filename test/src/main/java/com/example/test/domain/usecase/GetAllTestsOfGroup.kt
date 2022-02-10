package com.example.test.domain.usecase

import com.example.common.presentation.Pagination
import com.example.common.data.Result
import com.example.common.domain.model.TestDomainModel
import com.example.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetAllTestsOfGroup @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(page: Int, groupId: String): Result<Pagination<TestDomainModel>> {
        return testsRepo.getAllTests(page, groupId)
    }
}