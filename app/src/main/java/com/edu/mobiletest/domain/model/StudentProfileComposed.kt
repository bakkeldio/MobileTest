package com.edu.mobiletest.domain.model

import com.edu.common.domain.model.IProfile
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.group.domain.model.GroupDomain

data class StudentProfileComposed(
    val profile: StudentInfoDomain,
    val group: GroupDomain,
    val studentRating: Int
): IProfile