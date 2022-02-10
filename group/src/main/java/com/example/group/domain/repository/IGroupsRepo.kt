package com.example.group.domain.repository

import com.example.common.data.Result
import com.example.common.domain.model.TestDomainModel

interface IGroupsRepo {
    suspend fun getAvailableGroups(): Result<List<com.example.group.domain.model.GroupDomain>>

    suspend fun searchThroughGroups(query: String): Result<List<com.example.group.domain.model.GroupDomain>>

    suspend fun getGroupDetailInfo(groupId: String): Result<com.example.group.domain.model.GroupDomain>

    suspend fun checkIfHasAccessToEnterGroup(groupId: String): Result<Pair<com.example.group.domain.model.CoreRoleEnum, String?>>

    suspend fun saveUserRole(): Result<Unit>

    suspend fun enterGroup(groupId: String): Result<Unit>

    suspend fun leaveGroup(groupId: String): Result<Unit>

    suspend fun getGroupTests(groupId: String): Result<List<TestDomainModel>>
}