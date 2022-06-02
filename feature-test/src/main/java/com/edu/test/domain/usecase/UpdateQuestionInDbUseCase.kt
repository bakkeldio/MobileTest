package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateQuestionInDbUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {

    suspend operator fun invoke(questionDomain: QuestionWithAnswersDomain) =
        localTestRepo.updateQuestionInDb(questionDomain)
}