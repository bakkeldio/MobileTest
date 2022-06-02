package com.edu.mobiletest.ui.model

import com.edu.mobiletest.domain.model.TeacherProfileComposed

object TeacherProfileDomainToUIMapper : UIMapper<TeacherProfileComposed, ProfileTeacherUI> {
    override fun mapDomainModelToUI(domainModel: TeacherProfileComposed): ProfileTeacherUI {
        return ProfileTeacherUI(
            domainModel.teacher.id,
            domainModel.teacher.name
                ?: throw IllegalArgumentException("name of the teacher can't be null"),
            domainModel.teacher.position,
            domainModel.teacher.avatar,
            domainModel.groups
        )
    }
}