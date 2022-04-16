package com.edu.test.data.mapper

import com.edu.common.domain.ApiMapper
import com.edu.test.data.model.Teacher
import com.edu.test.domain.model.TeacherInfoDomain

object TeacherInfoMapperImpl: ApiMapper<Teacher, TeacherInfoDomain> {
    override fun mapToDomain(apiEntity: Teacher, uid: String): TeacherInfoDomain {
        return TeacherInfoDomain(
            id = apiEntity.id ?: throw IllegalArgumentException("id of teacher can't be null"),
            groupsIds = apiEntity.groupsIds,
            name = apiEntity.name,
            position = apiEntity.position
        )
    }
}