package com.edu.mobiletestadmin.data.repository

import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import kotlinx.coroutines.flow.Flow

interface IGroupRepo {

    fun getAllGroups(): Flow<ResultFirebase<List<UserGroup>>>

    suspend fun searchGroups(query: String): ResultFirebase<List<UserGroup>>

    suspend fun getGroupInfo(groupId: String): ResultFirebase<UserGroup>

    suspend fun createGroup(
        groupName: String,
        groupDescription: String,
        logoUri: String? = null
    ): ResultFirebase<String>

    suspend fun updateGroup(
        groupUid: String,
        groupName: String,
        groupDescription: String
    ): ResultFirebase<Boolean>

    suspend fun getGroupStudents(groupUid: String): Flow<ResultFirebase<List<User>>>

    suspend fun getGroupTeachers(groupUid: String): Flow<ResultFirebase<List<User>>>

    suspend fun deleteGroupLogo(groupUid: String): ResultFirebase<String?>

    suspend fun updateGroupLogo(groupUid: String, uri: String): ResultFirebase<String>

    suspend fun addUserToGroup(
        groupUid: String,
        userUid: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<Boolean>

    suspend fun deleteGroupUid(
        groupUid: String
    ): ResultFirebase<Unit>

    suspend fun deleteUsersFromGroup(
        groupUid: String,
        userIds: List<String>,
        usersTypeEnum: UserTypeEnum
    ): ResultFirebase<Unit>
}