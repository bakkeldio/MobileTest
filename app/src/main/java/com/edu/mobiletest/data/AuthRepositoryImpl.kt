package com.edu.mobiletest.data

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.model.NewUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) : IAuthRepository {
    override suspend fun signInUser(email: String, password: String): Result<FirebaseUser> {

        return try {
            withContext(Dispatchers.IO) {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                Result.Success(authResult.user)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signUpUser(userData: NewUserData): Result<FirebaseUser> {
        return try {
            withContext(Dispatchers.IO) {
                val data =
                    auth.createUserWithEmailAndPassword(userData.email, userData.password).await()
                Result.Success(data.user)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOutUser(): Result<Unit> {
        return try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                auth.sendPasswordResetEmail(email).await()
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}