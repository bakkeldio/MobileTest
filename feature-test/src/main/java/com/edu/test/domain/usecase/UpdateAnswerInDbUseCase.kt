package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateAnswerInDbUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {

    suspend operator fun invoke(answer: QuestionAnswerDomain) = localTestRepo.updateAnswer(answer)
}