package com.edu.mobiletest.data

import android.content.SharedPreferences
import android.net.Uri
import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TeacherProfile
import com.edu.group.data.mapper.GroupMapperImpl
import com.edu.group.data.model.Group
import com.edu.group.domain.model.GroupDomain
import com.edu.mobiletest.data.mappers.TeacherProfileToDomainMapper
import com.edu.mobiletest.domain.repository.IProfileRepository
import com.edu.test.domain.model.TeacherInfoDomain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun getStudentProfileInfo(): Result<StudentInfoDomain> {
        return try {
            val student = db.collection("students").document(auth.uid!!).get().await()
                .toObject(StudentInfoDomain::class.java)
                ?: throw IllegalArgumentException("student model can't be null")
            Result.Success(student)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTeacherProfileInfo(): Result<TeacherInfoDomain> {
        return try {
            val teacher = db.collection("teachers").document(auth.uid!!).get().await()
                .toObject(TeacherProfile::class.java) ?: throw IllegalArgumentException(
                "teacher model can't be null"
            )
            Result.Success(TeacherProfileToDomainMapper.mapToDomain(teacher))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGroupsByIds(ids: List<String>): Result<List<GroupDomain>> {
        return try {
            val groups = db.collection("groups").whereIn("uid", ids).get().await()
                .toObjects(Group::class.java)
            Result.Success(groups.map {
                GroupMapperImpl.mapToDomain(it)
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getProfileImage(): Flow<Result<String?>> {
        return callbackFlow {

            val isTeacher = sharedPreferences.getBoolean(
                "isUserAdmin",
                false
            )
            val listener =
                db.collection(if (isTeacher) "teachers" else "students").document(auth.uid!!)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        trySend(Result.Success(value?.get("avatarUrl") as String?))
                    }
            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.Default)
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

    override suspend fun getStudentRating(groupId: String): Result<Int> {
        return try {
            val index = db.collection("students")
                .whereEqualTo("groupId", groupId)
                .orderBy("overallScore", Query.Direction.DESCENDING).get().await().indexOfFirst {
                    it["uid"] == auth.uid
                }
            Result.Success(index + 1)
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
                            onComplete(student.registrationTokens)
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