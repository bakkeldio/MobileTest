package com.edu.mobiletest.domain.repository

import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.group.domain.model.GroupDomain
import com.edu.test.domain.model.TeacherInfoDomain
import com.edu.test.domain.model.dbModels.TestDomain
import kotlinx.coroutines.flow.Flow

interface IProfileRepository {

    suspend fun getStudentProfileInfo(): Result<StudentInfoDomain>

    suspend fun getTeacherProfileInfo(): Result<TeacherInfoDomain>

    suspend fun getGroupsByIds(ids: List<String>): Result<List<GroupDomain>>

    fun getProfileImage(): Flow<Result<String?>>

    suspend fun uploadProfilePhoto(url: String): Result<String>

    suspend fun deleteProfilePhoto(): Result<Unit>

    suspend fun getStudentRating(groupId: String): Result<Int>

    suspend fun updateProfileImageDB(url: String?): Result<Unit>

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit)

    fun updateFCMRegistrationTokens(registrationTokens: MutableList<String>)

    fun checkIfUserIsSignedIn(): Boolean

    suspend fun getCurrentRegistrationToken(): Result<String>

}