package com.edu.group.data.repository

import com.edu.common.data.Result
import com.edu.common.data.mapper.StudentInfoMapperImpl
import com.edu.common.data.model.Student
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.repository.IStudentsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class StudentsRepoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : IStudentsRepo {

    override fun getStudentsToAdd(): Flow<Result<List<StudentInfoDomain>>> {
        return callbackFlow {
            val listener = db.collection("students").whereEqualTo("groupId", null)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val students = value?.documents?.map {
                        val model = it.toObject(Student::class.java)
                            ?: throw IllegalArgumentException("Student model can't be null")
                        StudentInfoMapperImpl.mapToDomain(model, it.id)
                    }
                    trySend(Result.Success(students))
                }
            awaitClose {
                listener.remove()
            }
        }
    }


    override suspend fun addStudentToGroup(
        groupId: String,
        student: StudentInfoDomain
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val mainPath = db.collection("groups").document(groupId)
                db.runBatch { batch ->
                    batch.update(
                        db.collection("students").document(student.uid),
                        "groupId",
                        groupId
                    )
                    batch.update(mainPath, "studentsCount", FieldValue.increment(1))

                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getStudentsWithRatingInGroup(groupId: String): Flow<Result<List<StudentInfoDomain>>> =
        callbackFlow {
            val listener = db.collection("students").whereEqualTo("groupId", groupId)
                .orderBy("overallScore", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val students = value?.toObjects(StudentInfoDomain::class.java) ?: emptyList()
                    trySend(Result.Success(students))
                }
            awaitClose {
                listener.remove()
            }
        }

    override fun getStudentsOfGroup(): Flow<Result<List<StudentInfoDomain>>> {
        return callbackFlow {
            val result = db.collection("students").document(auth.uid!!).get().await()
            val student = result.toObject(Student::class.java)
                ?: throw IllegalArgumentException("Student model can not be null")
            val callback =
                db.collection("students").whereEqualTo("groupId", student.groupId!!)
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

    override suspend fun getStudentsByIds(ids: List<String>): Result<List<StudentInfoDomain>> {
        return try {

            val students = db.collection("students").whereIn("uid", ids).get().await()
                .toObjects(StudentInfoDomain::class.java)
            Result.Success(students)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchStudentsByQuery(query: String): Result<List<StudentInfoDomain>> {
        return try {
            val students =
                db.collection("students").orderBy("name").whereArrayContains(
                    "nameKeywords", query.lowercase()
                )
                    .get().await()
            Result.Success(students.toObjects(StudentInfoDomain::class.java))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}