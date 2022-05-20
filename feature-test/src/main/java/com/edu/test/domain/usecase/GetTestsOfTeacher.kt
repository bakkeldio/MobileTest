package com.edu.test.domain.usecase

import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetTestsOfTeacher @Inject constructor(private val testsRepo: ITestsRepository) {
    suspend operator fun invoke(groupId: String): Flow<TestsListState> {
        return testsRepo.getTestsOfTeacher(groupId)
    }
}