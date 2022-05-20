package com.edu.group.presentation.model

import com.edu.group.domain.model.CoreRoleEnum
import com.edu.group.domain.model.GroupDomain
import com.edu.common.domain.model.TestDomainModel

data class GroupData(
    val detail: GroupDomain,
    val role: CoreRoleEnum,
    val tests: List<TestDomainModel>
)