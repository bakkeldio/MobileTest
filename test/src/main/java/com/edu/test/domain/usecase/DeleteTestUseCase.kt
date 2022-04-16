package com.edu.test.domain.usecase

import com.edu.common.data.Result
import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteTestUseCase @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(groupId: String, testId: String): Result<Unit> {
        return testsRepo.deleteTest(groupId, testId)
    }
}