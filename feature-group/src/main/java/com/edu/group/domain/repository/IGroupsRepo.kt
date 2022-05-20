package com.edu.group.domain.repository

import com.edu.common.data.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TestDomainModel
import com.edu.group.domain.model.CoreRoleEnum
import com.edu.group.domain.model.GroupDomain
import kotlinx.coroutines.flow.Flow

interface IGroupsRepo {
    suspend fun getAvailableGroups(): Result<List<GroupDomain>>

    suspend fun searchThroughGroups(query: String): Result<List<GroupDomain>>

    suspend fun getGroupDetailInfo(groupId: String): Result<GroupDomain>

    suspend fun checkTheRoleOfTheUser(groupId: String): Result<CoreRoleEnum>

    suspend fun saveUserRole(): Result<Unit>

    suspend fun enterGroup(groupId: String): Result<Unit>

    suspend fun leaveGroup(groupId: String): Result<Unit>

    suspend fun getGroupTests(groupId: String): Result<List<TestDomainModel>>

    fun getStudentsOfGroup(groupId: String): Flow<Result<List<StudentInfoDomain>>>

    suspend fun deleteStudentFromGroup(groupId: String, studentId: String): Result<Unit>

    suspend fun updateGroupDetails(groupDomain: GroupDomain): Result<Unit>

    suspend fun uploadGroupAvatarToStorage(groupId: String, uri: String): Result<String>

    suspend fun updateGroupAvatarInDb(groupId: String, downloadUrl: String): Result<Unit>
}