package com.example.common.domain.group.repository

import com.example.common.data.Result
import com.example.common.domain.group.model.CoreRoleEnum
import com.example.common.domain.group.model.GroupDomain
import com.example.common.domain.test.model.TestDomainModel

interface IGroupsRepo {
    suspend fun getAvailableGroups(): Result<List<GroupDomain>>

    suspend fun searchThroughGroups(query: String): Result<List<GroupDomain>>

    suspend fun getGroupDetailInfo(groupId: String): Result<GroupDomain>

    suspend fun checkIfHasAccessToEnterGroup(groupId: String): Result<Pair<CoreRoleEnum, String?>>

    suspend fun saveUserRole(): Result<Unit>

    suspend fun enterGroup(groupId: String): Result<Unit>

    suspend fun leaveGroup(groupId: String): Result<Unit>

    suspend fun getGroupTests(groupId: String): Result<List<TestDomainModel>>
}