package com.example.test.domain.usecase

import com.example.common.data.Result
import com.example.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SubmitUserScore @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(testId: String, groupId: String, testTitle: String): Result<Unit>{
        return testsRepo.submitTestResultOfUser(testId, groupId, testTitle)
    }
}