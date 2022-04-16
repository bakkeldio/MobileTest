package com.edu.mobiletest.domain.model

import com.edu.common.domain.model.IProfile
import com.edu.group.domain.model.GroupDomain

data class StudentProfileComposed(
    val profile: StudentProfile,
    val group: GroupDomain
): IProfile