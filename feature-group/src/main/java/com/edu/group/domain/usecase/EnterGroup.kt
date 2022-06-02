package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

//TODO:Remove
@ViewModelScoped
class EnterGroup @Inject constructor(private val groupRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<Unit> {
        return groupRepo.enterGroup(groupId)
    }
}