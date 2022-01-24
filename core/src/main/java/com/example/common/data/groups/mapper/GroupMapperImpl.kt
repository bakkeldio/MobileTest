package com.example.common.data.groups.mapper

import com.example.common.domain.ApiMapper
import com.example.common.data.groups.model.Group
import com.example.common.domain.group.model.GroupDomain
import kotlin.IllegalArgumentException

internal object GroupMapperImpl: ApiMapper<Group, GroupDomain> {
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