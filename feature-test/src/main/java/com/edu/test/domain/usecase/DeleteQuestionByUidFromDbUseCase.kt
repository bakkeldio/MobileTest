package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteQuestionByUidFromDbUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {

    suspend operator fun invoke(questionId: Int) =
        localTestRepo.deleteQuestionById(questionId)

}