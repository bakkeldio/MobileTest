package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteStudentFromGroupUseCase @Inject constructor(private val groupsRepository: IGroupsRepo) {

    suspend operator fun invoke(groupId: String, studentUid: String): Result<Unit> {
        return groupsRepository.deleteStudentFromGroup(groupId, studentUid)
    }
}