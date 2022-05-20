package com.edu.mobiletestadmin.data.repository

import com.edu.mobiletestadmin.data.model.ResultFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepoImpl(private val auth: FirebaseAuth, private val db: FirebaseFirestore) :
    IAuthRepo {

    override suspend fun signInAdmin(email: String, password: String): ResultFirebase<Unit> {
        return try {
            val admin =
                db.collection("admins").whereEqualTo("email", email).get().await().firstOrNull()
            if (admin != null) {
                auth.signInWithEmailAndPassword(email, password).await()
                ResultFirebase.Success(Unit)
            } else {
                ResultFirebase.Error(Throwable("Вы не являетесь админом данного приложения"))
            }
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun logout(): ResultFirebase<Unit> {
        return try {
            auth.signOut()
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override fun isAdminSignedIn(): Boolean {
        return auth.currentUser != null
    }
}