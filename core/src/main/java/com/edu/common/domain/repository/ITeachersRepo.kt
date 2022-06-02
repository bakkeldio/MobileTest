package com.edu.common.domain.repository

import com.edu.common.domain.Result
import com.edu.common.domain.model.TeacherProfile

interface ITeachersRepo {

    suspend fun getTeachersByIds(ids: List<String>): Result<List<TeacherProfile>>

    suspend fun searchTeachers(query: String): Result<List<TeacherProfile>>
}