package com.edu.mobiletestadmin.data.repository

import android.net.Uri
import com.edu.mobiletestadmin.data.model.*
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupRepoImpl(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    IGroupRepo {

    override fun getAllGroups(): Flow<ResultFirebase<List<UserGroup>>> {
        return callbackFlow {
            val listener = db.collection("groups").addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val groupsInfos = value?.toObjects(GroupInfo::class.java) ?: emptyList()
                val groups = groupsInfos.map {
                    MappersImpl.mapGroupInfoToUserGroup(it)
                }

                trySend(ResultFirebase.Success(groups))
            }
            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun searchGroups(query: String): ResultFirebase<List<UserGroup>> {
        return try {
            val response = db.collection("groups").orderBy("groupName")
                .whereArrayContains("nameKeywords", query).get().await()
            ResultFirebase.Success(response.toObjects(GroupInfo::class.java).map {
                MappersImpl.mapGroupInfoToUserGroup(it)
            })
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun getGroupInfo(groupId: String): ResultFirebase<UserGroup> {
        return try {
            val info = db.collection("groups").document(groupId).get().await()
                .toObject(GroupInfo::class.java)
            info ?: throw IllegalArgumentException("group info can't be null")
            ResultFirebase.Success(MappersImpl.mapGroupInfoToUserGroup(info))
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun createGroup(
        groupName: String,
        groupDescription: String,
        logoUri: String?
    ): ResultFirebase<String> {
        return try {
            val uid = UUID.randomUUID().toString()
            val url = logoUri?.let {
                storage.reference.child("groupsImages/$uid")
                    .putFile(Uri.parse(logoUri))
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            db.collection("groups").document(uid).set(
                hashMapOf(
                    "uid" to uid,
                    "groupName" to groupName,
                    "description" to groupDescription,
                    "avatar" to url?.toString()
                )
            ).await()
            ResultFirebase.Success(uid)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun updateGroup(
        groupUid: String,
        groupName: String,
        groupDescription: String
    ): ResultFirebase<Boolean> {
        return try {
            db.collection("groups").document(groupUid)
                .update(mapOf("groupName" to groupName, "description" to groupDescription)).await()
            ResultFirebase.Success(true)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun getGroupStudents(groupUid: String): Flow<ResultFirebase<List<User>>> {
        return callbackFlow {
            val listener = db.collection("students").whereEqualTo("groupId", groupUid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val students = value?.toObjects(Student::class.java) ?: emptyList()
                    val users = students.map { student ->
                        MappersImpl.mapStudentModelToUser(student)
                    }
                    trySend(ResultFirebase.Success(users))
                }

            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getGroupTeachers(groupUid: String): Flow<ResultFirebase<List<User>>> {
        return callbackFlow {
            val listener = db.collection("teachers").whereArrayContains("groupIds", groupUid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val teachers = value?.toObjects(Teacher::class.java) ?: emptyList()

                    val users = teachers.map { teacher ->
                        MappersImpl.mapTeacherModelToUser(teacher)
                    }
                    trySend(ResultFirebase.Success(users))
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun deleteGroupLogo(groupUid: String): ResultFirebase<String?> {
        return try {
            storage.reference.child("groupsImages/$groupUid").delete().await()
            db.collection("groups").document(groupUid).update("groupAvatar", null).await()
            ResultFirebase.Success(null)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun updateGroupLogo(groupUid: String, uri: String): ResultFirebase<String> {
        return try {
            val url = storage.reference.child("groupsImages/$groupUid").putFile(Uri.parse(uri))
                .await().storage.downloadUrl.await()
            db.collection("groups").document(groupUid).update("groupAvatar", url.toString()).await()
            ResultFirebase.Success(url.toString())
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun addUserToGroup(
        groupUid: String,
        userUid: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<Boolean> {
        return try {
            when (userTypeEnum) {
                UserTypeEnum.TEACHER -> {
                    db.runTransaction { transaction ->
                        val groups = transaction.get(db.collection("teachers").document(userUid))
                            .toObject(Teacher::class.java)?.groupIds ?: emptyList()
                        val newGroups = mutableListOf<String>()
                        newGroups.addAll(groups)
                        newGroups.add(groupUid)
                        transaction.update(
                            db.collection("teachers").document(userUid),
                            "groupIds",
                            newGroups
                        )

                    }.await()
                }
                UserTypeEnum.STUDENT -> {
                    db.collection("students").document(userUid).update("groupId", groupUid).await()
                }
            }
            ResultFirebase.Success(true)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun deleteGroupUid(groupUid: String): ResultFirebase<Unit> {
        return try {
            db.collection("groups").document(groupUid).delete().await()
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun deleteUsersFromGroup(
        groupUid: String,
        userIds: List<String>,
        usersTypeEnum: UserTypeEnum
    ): ResultFirebase<Unit> {
        return try {
            when (usersTypeEnum) {
                UserTypeEnum.STUDENT -> {
                    db.runBatch { batch ->
                        userIds.forEach {
                            batch.update(db.collection("students").document(it), "groupId", null)
                        }
                    }.await()
                }
                UserTypeEnum.TEACHER -> {
                    db.runTransaction { transaction ->
                        userIds.forEach {
                            val docRef = db.collection("teachers").document(it)
                            val groupIds = transaction.get(docRef)
                                .toObject(Teacher::class.java)?.groupIds?.toMutableList()
                                ?: mutableListOf()
                            groupIds.remove(groupUid)
                            transaction.update(docRef, "groupIds", groupIds)
                        }
                    }.await()
                }
            }
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }
}