package com.example.group.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.group.model.GroupDomain
import com.example.common.domain.group.repository.IGroupsRepo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class GetGroupDetails @Inject constructor(private val repo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<GroupDomain> {
        return repo.getGroupDetailInfo(groupId)
    }
}