package com.edu.mobiletest.data

import android.content.SharedPreferences
import android.net.Uri
import com.edu.common.data.Result
import com.edu.common.domain.model.IProfile
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TeacherProfile
import com.edu.mobiletest.domain.model.ProfileType
import com.edu.mobiletest.domain.model.StudentProfile
import com.edu.mobiletest.domain.repository.IProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val firebaseMessaging: FirebaseMessaging,
    private val sharedPreferences: SharedPreferences
) : IProfileRepository {

    override suspend fun getProfileInfo(): Result<Pair<ProfileType, IProfile>> {
        return try {
            auth.uid ?: throw IllegalArgumentException("uid of current user can't be null")
            val isTeacher = sharedPreferences.getBoolean(
                "isUserAdmin",
                false
            )
            val data =
                db.collection(if (isTeacher) "teachers" else "students").document(auth.uid!!).get()
                    .await()
            val profile =
                (if (isTeacher) data.toObject(TeacherProfile::class.java) else data.toObject(
                    StudentProfile::class.java
                ))
                    ?: throw IllegalArgumentException("profile can't be null")
            Result.Success(
                Pair(
                    if (isTeacher) ProfileType.TEACHER else ProfileType.STUDENT, profile
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun uploadProfilePhoto(url: String): Result<String> {
        return try {
            val result =
                storage.reference.child("studentsImages/${auth.uid}").putFile(Uri.parse(url))
                    .await()
            val uri = result.storage.downloadUrl.await()
            Result.Success(uri.toString())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProfilePhoto(): Result<Unit> {
        return try {
            storage.reference.child("studentsImages/${auth.uid}").delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProfileImageDB(url: String?): Result<Unit> {
        return try {
            val path =
                if (sharedPreferences.getBoolean("isUserAdmin", false)) "teachers" else "students"
            db.collection(path).document(auth.uid!!).update("avatarUrl", url).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        db.collection("teachers").whereEqualTo("uid", auth.uid!!).get()
            .addOnSuccessListener { snapshot ->
                val isTeacher = !snapshot.isEmpty
                sharedPreferences.edit().putBoolean("isUserAdmin", isTeacher).apply()
                if (isTeacher) {
                    db.collection("teachers").document(auth.uid!!).get()
                        .addOnSuccessListener { documentSnapshot ->
                            val teacher = documentSnapshot.toObject(TeacherProfile::class.java)!!
                            sharedPreferences.edit().putString("currentUserName", teacher.name)
                                .apply()
                            onComplete(teacher.registrationTokens)
                        }
                } else {
                    db.collection("students").document(auth.uid!!).get()
                        .addOnSuccessListener { documentSnapshot ->
                            val student = documentSnapshot.toObject(StudentInfoDomain::class.java)!!
                            sharedPreferences.edit().putString("currentUserName", student.name)
                                .apply()
                            onComplete(student.fcmTokens)
                        }
                }
            }
    }

    override fun updateFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        if (sharedPreferences.getBoolean("isUserAdmin", false)) {
            db.collection("teachers").document(auth.uid!!)
                .update(mapOf("registrationTokens" to registrationTokens))
        } else {
            db.collection("students").document(auth.uid!!)
                .update(mapOf("registrationTokens" to registrationTokens))
        }
    }

    override fun checkIfUserIsSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getCurrentRegistrationToken(): Result<String> {
        return try {
            val result = firebaseMessaging.token.await()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}