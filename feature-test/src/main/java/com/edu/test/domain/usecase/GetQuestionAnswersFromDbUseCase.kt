package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetQuestionAnswersFromDbUseCase @Inject constructor(
    private val localTestRepo: ILocalTestsRepo
) {

    operator fun invoke(questionId: Int) = localTestRepo.getQuestionAnswers(questionId)
}