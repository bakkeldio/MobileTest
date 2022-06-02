package com.edu.mobiletest.data

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.model.UserRoleEnum

interface IRolesRepository {

    suspend fun updateRoleForUser(userId: String, role: UserRoleEnum): Result<Unit>
}