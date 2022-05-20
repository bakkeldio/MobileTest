package com.edu.group.domain.usecase

import com.edu.common.data.Result
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetGroupDetails @Inject constructor(private val repo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<GroupDomain> {
        return repo.getGroupDetailInfo(groupId)
    }
}