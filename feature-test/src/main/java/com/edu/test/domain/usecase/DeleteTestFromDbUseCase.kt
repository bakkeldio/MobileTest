package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteTestFromDbUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {

    suspend operator fun invoke(testDomain: TestDomain) = localTestRepo.deleteTest(testDomain)

}