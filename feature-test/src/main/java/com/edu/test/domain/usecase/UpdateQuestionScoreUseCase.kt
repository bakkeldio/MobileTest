package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateQuestionScoreUseCase @Inject constructor(private val testRepo: ITestsRepository) {

    suspend operator fun invoke(
        groupId: String,
        testId: String,
        questionId: String,
        studentUid: String,
        newScore: Int
    ) = testRepo.updateOpenQuestionScore(groupId, testId, questionId, studentUid, newScore)
}