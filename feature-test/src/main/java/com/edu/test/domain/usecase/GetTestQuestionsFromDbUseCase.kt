package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ILocalTestsRepo
import javax.inject.Inject

class GetTestQuestionsFromDbUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {

    operator fun invoke(testUid: String) = localTestRepo.getQuestionsOfTest(testUid)
}