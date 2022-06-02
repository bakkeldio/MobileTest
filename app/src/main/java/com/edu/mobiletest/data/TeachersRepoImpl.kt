package com.edu.mobiletest.data

import com.edu.common.domain.Result
import com.edu.common.domain.model.TeacherProfile
import com.edu.common.domain.repository.ITeachersRepo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeachersRepoImpl @Inject constructor(private val db: FirebaseFirestore) : ITeachersRepo {

    override suspend fun getTeachersByIds(ids: List<String>): Result<List<TeacherProfile>> {
        return try {
            val teachersResult = db.collection("teachers").whereIn("uid", ids).get().await()
            Result.Success(teachersResult.toObjects(TeacherProfile::class.java))
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun searchTeachers(query: String): Result<List<TeacherProfile>> {
        return try {
            val teachers =
                db.collection("teachers").orderBy("name")
                    .whereArrayContains("nameKeywords", query.lowercase())
                    .get().await()
            Result.Success(teachers.toObjects(TeacherProfile::class.java))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}