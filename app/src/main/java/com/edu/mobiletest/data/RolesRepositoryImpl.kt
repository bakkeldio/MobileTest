package com.edu.mobiletest.data

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.model.UserRoleEnum
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityRetainedScoped
class RolesRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): IRolesRepository {
    override suspend fun updateRoleForUser(userId: String, role: UserRoleEnum): Result<Unit> {
        return try {
            firestore.collection("roles").add(hashMapOf(userId to role.value)).await()
            Result.Success(Unit)
        }catch (e: Exception){
            Result.Error(e)
        }
    }
}