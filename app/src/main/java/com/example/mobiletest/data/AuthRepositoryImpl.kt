package com.example.mobiletest.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityRetainedScoped
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) : IAuthRepository {
    override suspend fun signInUser(email: String, password: String): Result<FirebaseUser> {

        return try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                Result.Success(authResult.user)
            } catch (e: Exception) {
                Result.Error(e)
            }
    }
}