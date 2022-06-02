package com.edu.mobiletest.domain.usecase

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.model.TeacherProfileComposed
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetTeachersProfileUseCase @Inject constructor(
    private val profileRepo: IProfileRepository
) {

    suspend operator fun invoke() =
        profileRepo.getTeacherProfileInfo().processResult(onSuccess = { teacher ->
            profileRepo.getGroupsByIds(teacher.groupsIds).processResult(onSuccess = { groups ->
                Result.Success(TeacherProfileComposed(groups, teacher))
            })
        })

}