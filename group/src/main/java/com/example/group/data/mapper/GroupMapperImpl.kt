package com.example.group.data.mapper

import com.example.common.domain.ApiMapper
import com.example.group.data.model.Group
import com.example.group.domain.model.GroupDomain
import kotlin.IllegalArgumentException

object GroupMapperImpl: ApiMapper<Group, GroupDomain> {
    override fun mapToDomain(apiEntity: Group): GroupDomain {
        return GroupDomain(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of the group can't be null"),
            groupName = apiEntity.group_name.orEmpty(),
            description = apiEntity.description.orEmpty(),
            studentsCount = apiEntity.students_count,
            testsCount = apiEntity.tests_count
        )
    }
}