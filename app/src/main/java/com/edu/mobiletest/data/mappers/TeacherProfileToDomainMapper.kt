package com.edu.mobiletest.data.mappers

import com.edu.common.data.mapper.Mapper
import com.edu.common.domain.model.TeacherProfile
import com.edu.test.domain.model.TeacherInfoDomain

object TeacherProfileToDomainMapper : Mapper.ToDomainMapper<TeacherProfile, TeacherInfoDomain> {
    override fun mapToDomain(model: TeacherProfile, uid: String): TeacherInfoDomain =
        TeacherInfoDomain(
            model.uid ?: throw IllegalArgumentException("uid of teacher can't be null"),
            model.groupIds ?: emptyList(),
            model.name,
            model.position,
            model.avatarUrl
        )

}