package com.edu.common.domain.repository

import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import kotlinx.coroutines.flow.Flow

interface IStudentsRepo {

    fun getStudentsToAdd(): Flow<Result<List<StudentInfoDomain>>>

    suspend fun addStudentToGroup(groupId: String, student: StudentInfoDomain): Result<Unit>

    fun getStudentsWithRatingInGroup(groupId: String): Flow<Result<List<StudentInfoDomain>>>

    fun getStudentsOfGroup(): Flow<Result<List<StudentInfoDomain>>>

    suspend fun getStudentsByIds(ids: List<String>): Result<List<StudentInfoDomain>>

    suspend fun searchStudentsByQuery(query: String): Result<List<StudentInfoDomain>>

}