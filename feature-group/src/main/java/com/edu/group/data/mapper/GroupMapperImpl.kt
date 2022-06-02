package com.edu.group.data.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.group.data.model.Group
import com.edu.group.domain.model.GroupDomain

object GroupMapperImpl: Mapper.ToDomainMapper<Group, GroupDomain> {
    override fun mapToDomain(model: Group, uid: String): GroupDomain {
        return GroupDomain(
            uid = uid,
            groupName = model.groupName.orEmpty(),
            description = model.description.orEmpty(),
            studentsCount = model.studentsCount,
            testsCount = model.testsCount,
            avatar = model.avatar
        )
    }
}