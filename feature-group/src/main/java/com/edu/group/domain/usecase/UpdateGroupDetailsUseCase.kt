package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateGroupDetailsUseCase @Inject constructor(private val groupRepo: IGroupsRepo) {

    suspend operator fun invoke(groupDomain: GroupDomain): Result<Unit> {
        return groupRepo.updateGroupDetails(groupDomain)
    }
}