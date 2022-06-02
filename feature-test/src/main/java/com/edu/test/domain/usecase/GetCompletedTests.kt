package com.edu.test.domain.usecase

import com.edu.common.domain.Result
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@ViewModelScoped
class GetCompletedTests @Inject constructor(val testsRepo: ITestsRepository) {

    suspend operator fun invoke(groupId: String): Flow<Result<List<PassedTestDomain>>> {
        return testsRepo.getCompletedTests(groupId).catch {
            emit(Result.Error(it))
        }
    }
}