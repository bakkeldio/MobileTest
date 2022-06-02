package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateTestInDbUseCase @Inject constructor(private val localRepo: ILocalTestsRepo) {

    suspend operator fun invoke(test: TestDomain){
        localRepo.updateTest(test)
    }
}