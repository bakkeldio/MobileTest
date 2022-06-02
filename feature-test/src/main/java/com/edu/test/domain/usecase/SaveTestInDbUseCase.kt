package com.edu.test.domain.usecase

import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SaveTestInDbUseCase @Inject constructor(private val repo: ILocalTestsRepo) {

    suspend operator fun invoke(domain: TestDomain)
         = repo.createTestWithInitialQuestion(domain)

}