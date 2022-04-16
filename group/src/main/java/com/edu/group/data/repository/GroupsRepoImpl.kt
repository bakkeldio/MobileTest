package com.edu.group.data.repository

import android.content.SharedPreferences
import android.net.Uri
import com.edu.common.data.Result
import com.edu.common.data.mapper.StudentInfoMapperImpl
import com.edu.common.data.mapper.TestMapperImpl
import com.edu.common.data.model.Student
import com.edu.common.data.model.Test
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TestDomainModel
import com.edu.group.data.mapper.GroupMapperImpl
import com.edu.group.data.model.Group
import com.edu.group.domain.model.CoreRoleEnum
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.repository.IGroupsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class GroupsRepoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences
) : IGroupsRepo {
    companion object {
        const val MAX_GROUP_LIMIT = 25
        const val TESTS_COUNT_FOR_GROUP_PAGE = 4
    }

    override suspend fun getAvailableGroups(): Result<List<GroupDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("groups").limit(MAX_GROUP_LIMIT.toLong()).get().await()
                Result.Success(response.documents.map {
                    val pair = Pair(
                        it.id,
                        it.toObject(Group::class.java)
                            ?: throw IllegalArgumentException("Group model(getAvailableGroups) can't be null")
                    )
                    GroupMapperImpl.mapToDomain(pair.second, pair.first)
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun searchThroughGroups(query: String): Result<List<GroupDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    db.collection("groups").orderBy("groupName").startAt(query)
                        .endAt(query + "\uf8ff")
                        .get().await()
                Result.Success(response.documents.map {
                    val pair = Pair(
                        it.id,
                        it.toObject(Group::class.java)
                            ?: throw IllegalArgumentException("Group document cannot be null")
                    )
                    GroupMapperImpl.mapToDomain(pair.second, pair.first)
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getGroupDetailInfo(groupId: String): Result<GroupDomain> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("groups").document(groupId).get().await()
                Result.Success(response.toObject(Group::class.java)?.let {
                    GroupMapperImpl.mapToDomain(
                        it, response.id
                    )
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun checkTheRoleOfTheUser(groupId: String): Result<CoreRoleEnum> {
        return withContext(Dispatchers.IO) {
            try {
                val isTeacher = !db.collection("groups").document(groupId).collection("teachers")
                    .whereEqualTo("uid", firebaseAuth.uid).get().await().isEmpty

                val isStudent = !db.collection("groups").document(groupId).collection("students")
                    .whereEqualTo("uid", firebaseAuth.uid).get().await().isEmpty
                val role = when {
                    isTeacher -> CoreRoleEnum.TEACHER
                    isStudent -> CoreRoleEnum.STUDENT
                    else -> CoreRoleEnum.NONE
                }
                Result.Success(role)

            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun saveUserRole(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("teachers").limit(1)
                    .whereEqualTo("uid", firebaseAuth.currentUser?.uid.orEmpty()).get().await()
                sharedPreferences.edit().putBoolean("isUserAdmin", response.size() == 1).apply()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun enterGroup(groupId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val groupStudentsRef =
                    db.collection("groups").document(groupId).collection("students")
                val groupRef = db.collection("groups").document(groupId)
                val studentsRef = db.collection("students").document(firebaseAuth.uid.orEmpty())
                db.runBatch {
                    groupStudentsRef.document(firebaseAuth.currentUser?.uid.orEmpty()).set(
                        hashMapOf(
                            "uid" to firebaseAuth.currentUser?.uid.orEmpty(),
                            "name" to firebaseAuth.currentUser?.displayName.orEmpty()
                        )
                    )
                    val updates = hashMapOf<String, Any>(
                        "students_count" to FieldValue.increment(1)
                    )
                    groupRef.update(updates)

                    studentsRef.update(hashMapOf<String, Any>("groupId" to groupId))
                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun leaveGroup(groupId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val groupStudentsRef =
                    db.collection("groups").document(groupId).collection("students")
                val groupRef = db.collection("groups").document(groupId)
                val studentsRef =
                    db.collection("students").document(firebaseAuth.currentUser?.uid ?: "")
                db.runBatch {
                    groupStudentsRef.document(firebaseAuth.currentUser?.uid.orEmpty()).delete()

                    groupRef.update(
                        hashMapOf<String, Any>(
                            "students_count" to FieldValue.increment(-1)
                        )
                    )

                    studentsRef.update(
                        hashMapOf<String, Any>(
                            "groupId" to FieldValue.delete()
                        )
                    )
                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getGroupTests(groupId: String): Result<List<TestDomainModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    db.collection("groups").document(groupId).collection("tests").limit(
                        TESTS_COUNT_FOR_GROUP_PAGE.toLong()
                    ).get()
                        .await()
                Result.Success(response.documents.mapNotNull {
                    val model = it.toObject(Test::class.java)
                        ?: throw IllegalArgumentException("Test model(getGroupTests) can't be null")
                    TestMapperImpl.mapToDomain(model, it.id)
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getStudentsOfGroup(groupId: String): Flow<Result<List<StudentInfoDomain>>> {
        return callbackFlow {
            val callback = db.collection("groups").document(groupId).collection("students")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(Result.Error(error))
                        close(error)
                        return@addSnapshotListener
                    }
                    try {
                        val tests = value?.documents?.map {
                            val model = it.toObject(Student::class.java)
                                ?: throw IllegalArgumentException("Student model can't be null")
                            StudentInfoMapperImpl.mapToDomain(model, it.id)
                        }
                        trySend(Result.Success(tests))
                    } catch (e: Exception) {
                        trySend(Result.Error(e))
                    }
                }
            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun deleteStudentFromGroup(groupId: String, studentId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val mainPath = db.collection("groups").document(groupId)
                db.runBatch { batch ->
                    batch.update(
                        mainPath,
                        "studentsCount",
                        FieldValue.increment(-1)
                    )

                    batch.delete(
                        mainPath.collection("students")
                            .document(studentId)
                    )

                    batch.update(db.collection("students").document(studentId), "groupId", null)
                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun updateGroupDetails(groupDomain: GroupDomain): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                db.collection("groups").document(groupDomain.uid).set(groupDomain).await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun uploadGroupAvatarToStorage(groupId: String, uri: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    firebaseStorage.reference.child("groupsImages/$groupId").putFile(Uri.parse(uri))
                        .await()
                val url = result.storage.downloadUrl.await()
                Result.Success(url.toString())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun updateGroupAvatarInDb(groupId: String, downloadUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                db.collection("groups").document(groupId).update("avatar", downloadUrl).await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

}