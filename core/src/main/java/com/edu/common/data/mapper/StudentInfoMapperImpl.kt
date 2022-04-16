package com.edu.common.data.mapper

import com.edu.common.data.model.Student
import com.edu.common.domain.ApiMapper
import com.edu.common.domain.model.StudentInfoDomain

object StudentInfoMapperImpl : ApiMapper<Student, StudentInfoDomain> {
    override fun mapToDomain(apiEntity: Student, uid: String): StudentInfoDomain {
        return StudentInfoDomain(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of student can't be null"),
            name = apiEntity.name ?: throw IllegalArgumentException("name of the student can't be null"),
            phone_number = apiEntity.phone_number,
            groupId = apiEntity.groupId,
            avatarUrl = apiEntity.avatar,
            overallScore = apiEntity.overall_score,

        )
    }
}