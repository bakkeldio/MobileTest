package com.edu.mobiletest.data

import com.edu.common.data.Result
import com.edu.mobiletest.domain.model.NewUserData
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {

    suspend fun signInUser(email: String, password: String): Result<FirebaseUser>

    suspend fun signUpUser(userData: NewUserData): Result<FirebaseUser>

    suspend fun signOutUser(): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}