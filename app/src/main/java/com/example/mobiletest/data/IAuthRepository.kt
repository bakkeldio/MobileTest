package com.example.mobiletest.data

import com.example.common.data.Result
import com.example.mobiletest.ui.model.NewUserData
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {

    suspend fun signInUser(email: String, password: String): Result<FirebaseUser>

    suspend fun signUpUser(userData: NewUserData): Result<FirebaseUser>

    suspend fun updateUsersData(userData: NewUserData): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}