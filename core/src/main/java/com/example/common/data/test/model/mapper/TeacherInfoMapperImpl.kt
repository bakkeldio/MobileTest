package com.example.common.data.test.model.mapper

import com.example.common.domain.ApiMapper
import com.example.common.data.test.model.Teacher
import com.example.common.domain.test.model.TeacherInfoDomain

internal object TeacherInfoMapperImpl: ApiMapper<Teacher, TeacherInfoDomain> {
    override fun mapToDomain(apiEntity: Teacher): TeacherInfoDomain {
        return TeacherInfoDomain(
            id = apiEntity.id ?: throw IllegalArgumentException("id of teacher can't be null"),
            groupsIds = apiEntity.groupsIds,
            name = apiEntity.name,
            position = apiEntity.position
        )
    }
}