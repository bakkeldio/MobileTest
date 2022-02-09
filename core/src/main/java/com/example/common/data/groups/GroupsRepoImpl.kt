package com.example.common.data.groups

import android.content.SharedPreferences
import com.example.common.data.Result
import com.example.common.data.groups.mapper.GroupMapperImpl
import com.example.common.data.groups.model.Group
import com.example.common.data.test.model.Student
import com.example.common.data.test.model.Test
import com.example.common.data.test.model.mapper.TestMapperImpl
import com.example.common.domain.group.model.CoreRoleEnum
import com.example.common.domain.group.model.GroupDomain
import com.example.common.domain.group.repository.IGroupsRepo
import com.example.common.domain.test.model.TestDomainModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class GroupsRepoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
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
                Result.Success(response.documents.mapNotNull {
                    it.toObject(Group::class.java)
                }.map {
                    GroupMapperImpl.mapToDomain(it)
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
                    db.collection("groups").orderBy("group_name").startAt(query)
                        .endAt(query + "\uf8ff")
                        .get().await()
                Result.Success(response.documents.mapNotNull {
                    it.toObject(Group::class.java)
                }.map {
                    GroupMapperImpl.mapToDomain(it)
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
                        it
                    )
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun checkIfHasAccessToEnterGroup(groupId: String): Result<Pair<CoreRoleEnum, String?>> {
        return withContext(Dispatchers.IO) {
            try {
                val isTeacher = sharedPreferences.getBoolean("isUserAdmin", false)

                val groupUid = if (!isTeacher) {
                    db.collection("students")
                        .document(firebaseAuth.uid ?: "")
                        .get().await().toObject(Student::class.java)?.groupId
                } else {
                    null
                }

                val role = if (isTeacher) CoreRoleEnum.TEACHER else CoreRoleEnum.STUDENT
                Result.Success(Pair(role, groupUid))

            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun saveUserRole(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("teachers").limit(1)
                    .whereEqualTo("id", firebaseAuth.currentUser?.uid.orEmpty()).get().await()
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
                        TESTS_COUNT_FOR_GROUP_PAGE.toLong()).get()
                        .await()
                Result.Success(response.documents.mapNotNull {
                    it.toObject(Test::class.java)
                }.map {
                    TestMapperImpl.mapToDomain(it)
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }


}