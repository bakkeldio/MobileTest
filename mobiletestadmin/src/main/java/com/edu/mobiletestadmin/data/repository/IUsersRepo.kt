package com.edu.mobiletestadmin.data.repository

import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.edu.mobiletestadmin.presentation.model.NewUser
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import kotlinx.coroutines.flow.Flow

interface IUsersRepo {

    fun getStudentsList(): Flow<ResultFirebase<List<User>>>

    fun getTeachersList(): Flow<ResultFirebase<List<User>>>

    suspend fun getUserGroups(
        uid: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<List<UserGroup>>

    suspend fun createNewUser(
        user: NewUser
    ): ResultFirebase<Unit>

    suspend fun getStudentEmail(
        uid: String
    ): ResultFirebase<String>

    suspend fun getUserGroupsByIds(
        groupsIds: List<String>
    ): ResultFirebase<List<Pair<String, String>>>

    suspend fun updateUserDetails(
        uid: String,
        user: NewUser
    ): ResultFirebase<Unit>

    suspend fun deleteUser(
        uid: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<Unit>

    suspend fun searchUsers(
        query: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<List<User>>

    suspend fun deleteStudents(
        uids: List<String>
    ): ResultFirebase<Unit>

    suspend fun deleteTeachers(
        uids: List<String>
    ): ResultFirebase<Unit>

}