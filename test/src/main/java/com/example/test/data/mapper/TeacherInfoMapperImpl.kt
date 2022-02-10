package com.example.test.data.mapper

import com.example.common.domain.ApiMapper
import com.example.test.data.model.Teacher
import com.example.test.domain.model.TeacherInfoDomain

object TeacherInfoMapperImpl: ApiMapper<Teacher, TeacherInfoDomain> {
    override fun mapToDomain(apiEntity: Teacher): TeacherInfoDomain {
        return TeacherInfoDomain(
            id = apiEntity.id ?: throw IllegalArgumentException("id of teacher can't be null"),
            groupsIds = apiEntity.groupsIds,
            name = apiEntity.name,
            position = apiEntity.position
        )
    }
}