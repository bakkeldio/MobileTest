package com.edu.mobiletestadmin.data.repository

import com.edu.mobiletestadmin.data.model.ResultFirebase

interface IAuthRepo {

    suspend fun signInAdmin(email: String, password: String): ResultFirebase<Unit>

    suspend fun logout(): ResultFirebase<Unit>

    fun isAdminSignedIn(): Boolean
}