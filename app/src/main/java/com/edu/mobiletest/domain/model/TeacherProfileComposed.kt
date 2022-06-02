package com.edu.mobiletest.domain.model

import com.edu.group.domain.model.GroupDomain
import com.edu.test.domain.model.TeacherInfoDomain
import com.edu.test.domain.model.dbModels.TestDomain

data class TeacherProfileComposed(
    val groups: List<GroupDomain>,
    val teacher: TeacherInfoDomain
)