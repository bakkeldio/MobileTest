package com.edu.mobiletest.ui.model

import com.edu.common.domain.model.TeacherProfile

object TeacherProfileDomainToUIMapper : UIMapper<TeacherProfile, ProfileTeacherUI> {
    override fun mapDomainModelToUI(domainModel: TeacherProfile): ProfileTeacherUI {
        return ProfileTeacherUI(
            domainModel.uid ?: throw IllegalArgumentException("uid of teacher can't be null"),
            domainModel.name ?: throw IllegalArgumentException("name of the teacher can't be null"),
            domainModel.position
                ?: throw IllegalArgumentException("position of teacher can't be null"),
            domainModel.avatarUrl
        )
    }
}