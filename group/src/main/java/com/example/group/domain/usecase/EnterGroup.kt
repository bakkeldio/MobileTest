package com.example.group.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.group.repository.IGroupsRepo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class EnterGroup @Inject constructor(private val groupRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<Unit>{
        return groupRepo.enterGroup(groupId)
    }
}