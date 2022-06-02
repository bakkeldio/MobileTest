package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetTestInfoUseCase @Inject constructor(
    private val testRepo: ILocalTestsRepo
) {

    suspend operator fun invoke(testUid: String) = testRepo.getTestInfo(testUid)
}