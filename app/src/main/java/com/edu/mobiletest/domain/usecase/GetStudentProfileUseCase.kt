package com.edu.mobiletest.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.repository.IGroupsRepo
import com.edu.mobiletest.domain.model.StudentProfileComposed
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetStudentProfileUseCase @Inject constructor(
    private val profileRepo: IProfileRepository,
    private val groupsRepo: IGroupsRepo
) {

    suspend operator fun invoke() =

        profileRepo.getStudentProfileInfo().processResult(onSuccess = { student ->
            groupsRepo.getGroupDetailInfo(
                student.groupId ?: throw IllegalArgumentException(
                    "groupId of student can't be null"
                )
            ).processResult(onSuccess = { group ->
                profileRepo.getStudentRating(group.uid).processResult(onSuccess = { rating ->
                    Result.Success(
                        StudentProfileComposed(
                            group = group,
                            profile = student,
                            studentRating = rating
                        )
                    )
                })
            })
        })

}