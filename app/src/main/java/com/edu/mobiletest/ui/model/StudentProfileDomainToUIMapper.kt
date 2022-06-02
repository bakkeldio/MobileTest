package com.edu.mobiletest.ui.model


import com.edu.mobiletest.domain.model.StudentProfileComposed

object StudentProfileDomainToUIMapper : UIMapper<StudentProfileComposed, ProfileStudentUI> {
    override fun mapDomainModelToUI(domainModel: StudentProfileComposed): ProfileStudentUI {
        return ProfileStudentUI(
            domainModel.profile.name,
            domainModel.profile.avatarUrl,
            domainModel.group.groupName,
            domainModel.group.uid,
            domainModel.profile.overallScore,
            domainModel.studentRating
        )
    }
}