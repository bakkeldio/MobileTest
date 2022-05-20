package com.edu.mobiletestadmin.data.repository

import com.edu.mobiletestadmin.data.model.*
import com.edu.mobiletestadmin.presentation.model.NewUser
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UsersRepoImpl(private val db: FirebaseFirestore, private val functions: FirebaseFunctions) :
    IUsersRepo {
    override fun getStudentsList(): Flow<ResultFirebase<List<User>>> {
        return callbackFlow {
            val listener =
                db.collection("students").orderBy("name").addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(ResultFirebase.Error(error))
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
        }
    }

    override fun getTeachersList(): Flow<ResultFirebase<List<User>>> {
        return callbackFlow {
            val listener =
                db.collection("teachers").orderBy("name").addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(ResultFirebase.Error(error))
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

    override suspend fun getUserGroups(
        uid: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<List<UserGroup>> {
        return try {
            when (userTypeEnum) {
                UserTypeEnum.STUDENT -> {
                    val student = db.collection("students").document(uid).get().await()
                        .toObject(Student::class.java)
                        ?: throw IllegalArgumentException("student model can't be null")
                    val groupInfo = db.collection("groups").document(student.groupId).get().await()
                        .toObject(GroupInfo::class.java)
                        ?: throw IllegalArgumentException("group model can't be null")
                    ResultFirebase.Success(listOf(MappersImpl.mapGroupInfoToUserGroup(groupInfo)))
                }
                UserTypeEnum.TEACHER -> {
                    val teacher = db.collection("teachers").document(uid).get().await()
                        .toObject(Teacher::class.java)
                        ?: throw IllegalArgumentException("teacher model can't be null")
                    val groups = teacher.groupIds.map {
                        val groupInfo = db.collection("groups").document(it).get().await()
                            .toObject(GroupInfo::class.java)
                            ?: throw IllegalArgumentException("group model can't be null")
                        MappersImpl.mapGroupInfoToUserGroup(groupInfo)
                    }
                    ResultFirebase.Success(groups)
                }
            }
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun getUserGroupsByIds(groupsIds: List<String>): ResultFirebase<List<Pair<String, String>>> {
        return try {
            val groupsDocuments =
                db.collection("groups").whereIn("uid", groupsIds).get().await()
            ResultFirebase.Success(groupsDocuments.map {
                val group = it.toObject(GroupInfo::class.java)
                Pair(
                    group.uid ?: throw IllegalArgumentException("group uid can't be null"),
                    group.groupName ?: throw IllegalArgumentException("group name can't be null")
                )
            })
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun updateUserDetails(uid: String, user: NewUser): ResultFirebase<Unit> {
        return try {
            functions.getHttpsCallable(
                if (user.userType == UserTypeEnum.STUDENT)
                    "updateStudentDetails" else "updateTeacherDetails"
            )
                .call(
                    hashMapOf(
                        "uid" to uid,
                        "email" to user.email,
                        "password" to user.password,
                        "nameKeywords" to user.nameKeywords,
                        "name" to user.name,
                        if (user.userType == UserTypeEnum.STUDENT) {
                            "groupId" to if (user.groups.isEmpty()) null else user.groups.first()
                        } else {
                            "groupIds" to user.groups
                        }
                    )
                ).await()
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun deleteUser(uid: String, userTypeEnum: UserTypeEnum): ResultFirebase<Unit> {
        return try {
            functions.getHttpsCallable("deleteUserByUid")
                .call(hashMapOf("uid" to uid, "role" to userTypeEnum.type)).await()
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun searchUsers(
        query: String,
        userTypeEnum: UserTypeEnum
    ): ResultFirebase<List<User>> {
        return try {
            val usersList = when (userTypeEnum) {
                UserTypeEnum.STUDENT -> {
                    db.collection("students").orderBy("name").whereArrayContains(
                        "nameKeywords", if (query.isEmpty()) " " else query.lowercase()
                    ).get().await().toObjects(Student::class.java).map { student ->
                        MappersImpl.mapStudentModelToUser(student)
                    }
                }
                UserTypeEnum.TEACHER -> {
                    db.collection("teachers").orderBy("name").whereArrayContains(
                        "nameKeywords", if (query.isEmpty()) " " else query.lowercase()
                    ).get().await().toObjects(Teacher::class.java).map { teacher ->
                        MappersImpl.mapTeacherModelToUser(teacher)
                    }
                }
            }
            ResultFirebase.Success(usersList)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun deleteStudents(uids: List<String>): ResultFirebase<Unit> {
        return try {
            functions.getHttpsCallable("deleteStudentsByUids").call(hashMapOf("uids" to uids))
                .await()
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun deleteTeachers(uids: List<String>): ResultFirebase<Unit> {
        return try {
            functions.getHttpsCallable("deleteTeachersByUids").call(hashMapOf("uids" to uids))
            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun createNewUser(user: NewUser): ResultFirebase<Unit> {
        return try {
            functions.getHttpsCallable("createUserWithRole").call(
                hashMapOf(
                    "name" to user.name,
                    "email" to user.email,
                    "pass" to user.password,
                    "nameKeywords" to user.nameKeywords,
                    "role" to user.userType.type,
                    "groupIds" to user.groups
                )
            ).await()

            ResultFirebase.Success(Unit)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }

    override suspend fun getStudentEmail(uid: String): ResultFirebase<String> {
        return try {
            val response = functions.getHttpsCallable("getStudetInfo").call(
                hashMapOf("uid" to uid)
            ).await()
            val data = (response.data as? HashMap<String, String?>)
                ?: throw IllegalArgumentException("response for email can't be null")
            val email =
                data["email"] ?: throw  IllegalArgumentException("email of the user can't be null")
            ResultFirebase.Success(email)
        } catch (e: Exception) {
            ResultFirebase.Error(e)
        }
    }
}