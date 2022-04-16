package com.edu.mobiletest.domain.usecase

import com.edu.common.data.Result
import com.edu.group.domain.repository.IGroupsRepo
import com.edu.common.domain.model.IProfile
import com.edu.mobiletest.domain.model.StudentProfileComposed
import com.edu.mobiletest.domain.model.ProfileType
import com.edu.mobiletest.domain.model.StudentProfile
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ProfileInfoUseCase @Inject constructor(
    private val groupsRepo: IGroupsRepo,
    private val profileRepo: IProfileRepository
) {
    suspend operator fun invoke(): Result<IProfile> {
        return try {
            when (val profileResponse = profileRepo.getProfileInfo()) {
                is Result.Success -> {
                    val data =
                        profileResponse.data
                            ?: throw IllegalArgumentException("profile data can't be empty")

                    if (data.first == ProfileType.TEACHER) {
                        return Result.Success(
                            data.second
                        )
                    } else {
                        val groupId = (data.second as StudentProfile).groupId
                            ?: throw IllegalArgumentException("group id of student can't be null")
                        when (val groupInfo = groupsRepo.getGroupDetailInfo(groupId)) {
                            is Result.Success -> {
                                Result.Success(
                                    StudentProfileComposed(
                                        data.second as StudentProfile,
                                        groupInfo.data
                                            ?: throw IllegalArgumentException("group info can't be null")
                                    )
                                )
                            }
                            is Result.Error -> {
                                groupInfo
                            }
                        }
                    }
                }
                is Result.Error -> {
                    profileResponse
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}