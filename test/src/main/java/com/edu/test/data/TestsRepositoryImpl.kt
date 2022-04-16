package com.edu.test.data

import android.content.SharedPreferences
import com.edu.common.data.Result
import com.edu.common.data.mapper.TestMapperImpl
import com.edu.common.data.model.Student
import com.edu.common.data.model.Test
import com.edu.common.domain.model.TestDomainModel
import com.edu.common.domain.model.TestResultDomain
import com.edu.test.data.datamanager.TestCreationHandler
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.data.mapper.TestResultMapper
import com.edu.test.data.model.Teacher
import com.edu.test.data.model.TestResult
import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.repository.ITestsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ActivityRetainedScoped
class TestsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) :
    ITestsRepository {

    override suspend fun checkIfHasAccessToCreateTask(): Boolean {
        return sharedPreferences.getBoolean("isUserAdmin", false)
    }

    override suspend fun getAllTests(
        groupId: String
    ): Flow<TestsListState> {
        return callbackFlow {
            val callback =
                db.collection("groups").document(groupId).collection("tests")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .orderBy("title")
                    .addSnapshotListener { value, error ->
                        trySend(TestsListState.Loading)
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        try {
                            val testsSnapshot = value?.documents ?: emptyList()
                            val tests = testsSnapshot.map {
                                val model =
                                    it.toObject(Test::class.java)
                                        ?: throw IllegalArgumentException("Test model can't be null")
                                TestMapperImpl.mapToDomain(model, it.id)
                            }
                            trySend(TestsListState.Success(tests))
                        } catch (e: Exception) {
                            trySend(TestsListState.Error(e))
                        }
                    }
            awaitClose {
                callback.remove()
            }
        }.flowOn(Dispatchers.IO)

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
        groupId: String,
        isUserAdmin: Boolean,
        isSearchCompletedTests: Boolean,
    ): Result<List<TestDomainModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val queryUser =
                    db.collection("groups")
                        .document(groupId)
                        .collection("tests")
                        .orderBy("title")
                        .startAt(query)
                        .endAt(query + "\uf8ff")


                val queryAdmin = db.collection("groups").document(groupId).collection("tests")
                    .whereEqualTo("authorUid", auth.uid)
                    .orderBy("title")
                    .startAt(query)
                    .endAt(query + "\uf8ff")

                val queryCompletedTests =
                    db.collection("groups").document(groupId).collection("students")
                        .document(auth.uid!!).collection("tests").orderBy("title").startAt(query)
                        .endAt(query + "\uf8ff")

                val response =
                    if (isUserAdmin) queryAdmin.get()
                        .await() else if (isSearchCompletedTests) queryCompletedTests.get()
                        .await() else queryUser.get().await()

                val results = response.documents.map {
                    TestMapperImpl.mapToDomain(
                        it.toObject(Test::class.java)
                            ?: throw IllegalArgumentException("Test model can't be null"), it.id
                    )
                }
                Result.Success(results)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun submitTestResultOfUser(
        testId: String,
        groupId: String,
        testTitle: String,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val totalScore = TestProcessHandler.calculateTotalScore()
                val mainRef = db.collection("groups").document(groupId).collection("students")
                    .document(auth.uid!!)
                db.runBatch { batch ->
                    batch.set(
                        mainRef.collection("tests").document(testId),
                        TestResult(
                            testTitle,
                            totalScore,
                            TestProcessHandler.mapTo(),
                            TestProcessHandler.getAnswersToOpenQuestions()
                        )
                    )
                    batch.update(
                        mainRef,
                        "overall_score",
                        FieldValue.increment(totalScore.toDouble())
                    )
                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getCompletedTests(groupId: String): Flow<TestsListState> {
        return callbackFlow {

            val callback = db.collection("groups").document(groupId).collection("students")
                .document(auth.uid!!).collection("tests").addSnapshotListener { value, error ->
                    if (error != null) {
                        cancel(error.localizedMessage ?: "")
                        return@addSnapshotListener
                    }

                    val tests = value?.documents?.map {
                        TestMapperImpl.mapToDomain(
                            it.toObject(Test::class.java)
                                ?: throw IllegalArgumentException("Test model can't be null"), it.id
                        )
                    }
                    trySend(TestsListState.Success(tests ?: emptyList()))
                }

            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun getCompletedTestQuestions(
        groupId: String,
        testId: String
    ): Result<TestResultDomain> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("groups").document(groupId).collection("students")
                    .document(auth.uid!!).collection("tests").document(testId).get().await()
                val result = response.toObject(TestResult::class.java)?.let {
                    TestResultMapper.mapToDomain(
                        it,
                        response.id
                    )
                }
                Result.Success(result)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun createTest(groupId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = db.collection("groups").document(groupId).collection("tests").add(
                    Test(
                        authorUid = auth.uid,
                        date = TestCreationHandler.getNewTest()?.availableDate,
                        time = TestCreationHandler.getNewTest()?.duration,
                        maxPoint = TestCreationHandler.getMaxPointForNewTest(),
                        title = TestCreationHandler.getNewTest()?.testName
                    )
                ).await()
                db.runBatch {
                    TestCreationHandler.getNewTest()?.questions?.forEach {
                        db.collection("groups").document(groupId).collection("tests")
                            .document(response.id).collection("questions")
                            .add(it)
                    }
                }.await()
                TestCreationHandler.clear()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getTestsOfTeacher(groupId: String) = callbackFlow {

        val callback = db.collection("groups").document(groupId).collection("tests")
            .whereEqualTo("authorUid", auth.uid).addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    trySend(TestsListState.Error(error))
                    return@addSnapshotListener
                }

                trySend(TestsListState.Success(value?.documents?.map {
                    TestMapperImpl.mapToDomain(
                        it.toObject(Test::class.java)
                            ?: throw IllegalArgumentException("Test model can't be null"), it.id
                    )
                } ?: emptyList())
                )
            }
        awaitClose {
            callback.remove()
        }
    }

    override suspend fun deleteTest(groupId: String, testId: String): Result<Unit> {
        return try {
            db.collection("groups").document(groupId).collection("tests").document(testId)
                .delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}