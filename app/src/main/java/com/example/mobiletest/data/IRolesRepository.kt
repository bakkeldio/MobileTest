package com.example.mobiletest.data

import com.example.common.data.Result

interface IRolesRepository {

    suspend fun updateRoleForUser(userId: String, role: UserRoleEnum): Result<Unit>
}