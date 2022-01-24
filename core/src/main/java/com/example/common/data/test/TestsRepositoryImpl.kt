package com.example.common.data.test

import android.content.SharedPreferences
import com.example.common.presentation.Pagination
import com.example.common.data.Result
import com.example.common.data.groups.model.Group
import com.example.common.data.test.model.Student
import com.example.common.data.test.model.Teacher
import com.example.common.data.test.model.Test
import com.example.common.data.test.model.mapper.TestMapperImpl
import com.example.common.domain.test.model.TestDomainModel
import com.example.common.domain.test.repository.ITestsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
internal class TestsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) :
    ITestsRepository {

    private var lastTestsSnapshots: List<DocumentSnapshot> = emptyList()

    override suspend fun checkIfHasAccessToCreateTask(): Boolean {
        return sharedPreferences.getBoolean("isUserAdmin", false)
    }

    override suspend fun getAllTests(
        page: Int,
        groupId: String
    ): Result<Pagination<TestDomainModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val totalCount = db.collection("groups").document(groupId).get().await()
                    .toObject(Group::class.java)?.students_count ?: 0

                val queryRef = db.collection("groups").document(groupId).collection("tests")
                    .limit(Pagination.DEFAULT_PAGE_SIZE.toLong())
                val currentPageSnapshots = if (page == 1) {
                    queryRef.get().await()
                } else {
                    queryRef.startAfter(lastTestsSnapshots[lastTestsSnapshots.lastIndex]).get()
                        .await()
                }
                lastTestsSnapshots = currentPageSnapshots.documents
                Result.Success(
                    Pagination(
                        currentPageSnapshots.documents.mapNotNull {
                            it.toObject(Test::class.java)
                        }.map {
                            TestMapperImpl.mapToDomain(it)
                        },
                        hasNextPage = totalCount > page * Pagination.DEFAULT_PAGE_SIZE
                    )
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getCurrentUserGroup(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    db.collection("students").whereEqualTo("uid", auth.currentUser?.uid).limit(1)
                        .get()
                        .await()
                if (response.documents.size == 0) {
                    val res =
                        db.collection("teachers").whereEqualTo("id", auth.currentUser?.uid).limit(1)
                            .get().await()
                    Result.Success(
                        res.documents.firstOrNull()
                            ?.toObject(Teacher::class.java)?.groupsIds?.firstOrNull()
                    )
                } else {
                    Result.Success(
                        response.firstOrNull()?.toObject(Student::class.java)?.groupId
                    )
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }


    override suspend fun searchTests(
        query: String,
        groupId: String
    ): Result<List<TestDomainModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    db.collection("groups")
                        .document(groupId)
                        .collection("tests")
                        .orderBy("title")
                        .startAt(query)
                        .endAt(query + "\uf8ff")
                        .limit(Pagination.DEFAULT_PAGE_SIZE.toLong()).get()
                        .await()

                val results = response.documents.asSequence().mapNotNull {
                    it.toObject(Test::class.java)
                }.map {
                    TestMapperImpl.mapToDomain(it)
                }.toList()
                Result.Success(results)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

}