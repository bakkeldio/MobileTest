package com.edu.group.domain.usecase

import com.edu.common.data.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetStudentsOfGroupUseCase @Inject constructor(private val groupsRepo: IGroupsRepo) {

    operator fun invoke(groupId: String): Flow<Result<List<StudentInfoDomain>>> {
        return groupsRepo.getStudentsOfGroup(groupId)
    }
}