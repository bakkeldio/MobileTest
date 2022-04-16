package com.edu.group.domain.usecase

import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateGroupAvatarInDBUseCase @Inject constructor(private val groupRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String, downloadUrl: String) =
        groupRepo.updateGroupAvatarInDb(groupId, downloadUrl)
}