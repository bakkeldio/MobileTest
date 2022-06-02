package com.edu.common.data.mapper

import com.edu.common.data.model.Student
import com.edu.common.domain.model.StudentInfoDomain

object StudentInfoMapperImpl : Mapper.ToDomainMapper<Student, StudentInfoDomain> {
    override fun mapToDomain(model: Student, uid: String): StudentInfoDomain {
        return StudentInfoDomain(
            uid = model.uid ?: throw IllegalArgumentException("id of student can't be null"),
            name = model.name ?: throw IllegalArgumentException("name of the student can't be null"),
            phone_number = model.phone_number,
            groupId = model.groupId,
            avatarUrl = model.avatar,
            overallScore = model.overall_score,

        )
    }
}