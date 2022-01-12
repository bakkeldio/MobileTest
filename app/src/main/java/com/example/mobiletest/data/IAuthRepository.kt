package com.example.mobiletest.data

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

    suspend fun signInUser(email: String, password: String): Result<FirebaseUser>
}