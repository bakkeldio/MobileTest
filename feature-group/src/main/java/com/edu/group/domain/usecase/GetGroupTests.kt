package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.repository.IGroupsRepo
import com.edu.common.domain.model.TestDomainModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetGroupTests @Inject constructor(private val groupsRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<List<TestDomainModel>> {
        return groupsRepo.getGroupTests(groupId)
    }
}