package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetStudentsWhoPassedTest @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(testId: String) = testsRepo.getStudentsWhoPassedTheTest(testId)
}