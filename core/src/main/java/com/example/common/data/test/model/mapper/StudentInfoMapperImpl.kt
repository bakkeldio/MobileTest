package com.example.common.data.test.model.mapper

import com.example.common.domain.ApiMapper
import com.example.common.data.test.model.Student
import com.example.common.domain.test.model.StudentInfoDomain

internal object StudentInfoMapperImpl: ApiMapper<Student, StudentInfoDomain> {
    override fun mapToDomain(apiEntity: Student): StudentInfoDomain {
        return StudentInfoDomain(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of student can't be null"),
            groupId = apiEntity.groupId ?: throw IllegalArgumentException("groupId can't be null"),
            name = apiEntity.name,
            phone_number = apiEntity.phone_number
        )
    }
}