package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class InsertAnswerToDbUseCase @Inject constructor(private val localRepo: ILocalTestsRepo) {

    suspend operator fun invoke(answer: QuestionAnswerDomain) = localRepo.insertAnswer(answer)
}