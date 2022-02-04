package com.example.group.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.group.repository.IGroupsRepo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LeaveGroup @Inject constructor(private val groupRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<Unit> {
        return groupRepo.leaveGroup(groupId)
    }
}