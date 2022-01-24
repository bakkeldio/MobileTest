package com.example.group.presentation.model

import com.example.common.domain.group.model.CoreRoleEnum
import com.example.common.domain.group.model.GroupDomain
import com.example.common.domain.test.model.TestDomainModel

data class GroupData(
    val detail: GroupDomain,
    val userGroup: String?,
    val role: CoreRoleEnum,
    val tests: List<TestDomainModel>
)