package com.edu.group.data.mapper

import com.edu.common.domain.ApiMapper
import com.edu.group.data.model.Group
import com.edu.group.domain.model.GroupDomain

object GroupMapperImpl: ApiMapper<Group, GroupDomain> {
    override fun mapToDomain(apiEntity: Group, uid: String): GroupDomain {
        return GroupDomain(
            uid = uid,
            groupName = apiEntity.groupName.orEmpty(),
            description = apiEntity.description.orEmpty(),
            studentsCount = apiEntity.studentsCount,
            testsCount = apiEntity.testsCount,
            avatar = apiEntity.avatar
        )
    }
}