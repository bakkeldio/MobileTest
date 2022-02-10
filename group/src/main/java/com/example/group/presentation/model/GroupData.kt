package com.example.group.presentation.model

import com.example.group.domain.model.CoreRoleEnum
import com.example.group.domain.model.GroupDomain
import com.example.common.domain.model.TestDomainModel

data class GroupData(
    val detail: GroupDomain,
    val userGroup: String?,
    val role: CoreRoleEnum,
    val tests: List<TestDomainModel>
)