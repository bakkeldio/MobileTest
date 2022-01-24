package com.example.test.domain.usecase

import com.example.common.presentation.Pagination
import com.example.common.data.Result
import com.example.common.domain.test.model.TestDomainModel
import com.example.common.domain.test.repository.ITestsRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
internal class GetAllTestsOfGroup @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(page: Int, groupId: String): Result<Pagination<TestDomainModel>> {
        return testsRepo.getAllTests(page, groupId)
    }
}