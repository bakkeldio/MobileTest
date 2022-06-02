package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ILocalTestsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetUnPublishedTestsUseCase @Inject constructor(private val localTestRepo: ILocalTestsRepo) {


    operator fun invoke(groupUid: String)  = localTestRepo.getUnPublishedTests(groupUid)
}