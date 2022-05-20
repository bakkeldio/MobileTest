package com.edu.test.data

import android.content.SharedPreferences
import com.edu.common.data.Result
import com.edu.common.data.mapper.TestMapperImpl
import com.edu.common.data.model.Student
import com.edu.common.data.model.Test
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TestDomainModel
import com.edu.common.utils.getKeywords
import com.edu.test.data.datamanager.TestCreationHandler
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.data.mapper.TestResultMapper
import com.edu.test.data.model.Teacher
import com.edu.test.data.model.TestResult
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.model.TestResultDomain
import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.repository.ITestsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
        isUserAdmin: Boolean
    ): Result<List<TestDomainModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val queryUser =
                    db.collection("groups")
                        .document(groupId)
                        .collection("tests")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .whereArrayContains("titleKeywords", query.lowercase())


                val queryAdmin = db.collection("groups")
                    .document(groupId)
                    .collection("tests")
                    .whereEqualTo("authorUid", auth.uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .whereArrayContains("titleKeywords", query.lowercase())


                val response =
                    if (isUserAdmin) queryAdmin.get()
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

    override suspend fun searchThroughCompletedTests(
        query: String,
        groupId: String
    ): Result<List<PassedTestDomain>> {
        return withContext(Dispatchers.IO) {
            try {

                val testResults =
                    db.collectionGroup("passedStudents")
                        .whereEqualTo("studentUid", auth.uid!!)
                        .whereArrayContains("testTitleKeywords", query.lowercase())
                        .get()
                        .await()
                        .toObjects(TestResult::class.java)

                val completedTests = testResults.map { testResult ->
                    PassedTestDomain(
                        testResult.testUid,
                        testResult.testTitle
                            ?: throw IllegalArgumentException("test title can't be null"),
                        testResult.testDate
                    )
                }
                Result.Success(completedTests)

            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun submitTestResultOfUser(
        testId: String,
        groupId: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val totalScore = TestProcessHandler.calculateTotalScore()
                val student = db.collection("students").document(auth.uid!!).get().await()
                    .toObject(StudentInfoDomain::class.java)
                    ?: throw IllegalArgumentException("student model can't be null")
                db.runTransaction { transaction ->
                    val documentSnapshot = transaction.get(
                        db.collection("groups").document(groupId).collection("tests")
                            .document(testId)
                    )
                    val test = documentSnapshot.toObject(Test::class.java)
                        ?: throw IllegalArgumentException("test model can't be null")
                    transaction.set(
                        db.collection("groups").document(groupId).collection("tests")
                            .document(testId).collection("passedStudents").document(auth.uid!!),
                        TestResult(
                            student.uid,
                            student.name,
                            student.avatarUrl,
                            documentSnapshot.id,
                            test.title
                                ?: throw IllegalArgumentException("test title can't be null"),
                            test.title?.lowercase()?.getKeywords() ?: emptyList(),
                            test.date,
                            totalScore,
                            groupId,
                            TestProcessHandler.mapTo(),
                            TestProcessHandler.getAnswersToOpenQuestions()
                        )
                    )
                    transaction.update(
                        db.collection("students").document(auth.uid!!),
                        "overallScore",
                        FieldValue.increment(totalScore.toDouble())
                    )
                }.await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getStudentsWhoPassedTheTest(
        testId: String
    ): Flow<Result<List<TestResultDomain>>> {
        return callbackFlow {

            val callback = db.collectionGroup("passedStudents").whereEqualTo("testUid", testId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val results = value?.toObjects(TestResult::class.java) ?: emptyList()
                    val resultsDomain = results.map {
                        TestResultDomain(
                            it.studentUid,
                            it.studentName,
                            it.studentAvatar,
                            it.totalPoints
                        )
                    }
                    trySend(Result.Success(resultsDomain))
                }

            awaitClose {
                callback.remove()
            }
        }
    }

    override suspend fun getCompletedTests(groupId: String): Flow<Result<List<PassedTestDomain>>> {
        return callbackFlow {
            val callback = db.collectionGroup("passedStudents")
                .whereEqualTo("groupUid", groupId)
                .whereEqualTo("studentUid", auth.uid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val tests = value?.documents?.map {
                        val testResult = it.toObject(TestResult::class.java)
                            ?: throw IllegalArgumentException("testResult model can't be null")
                        PassedTestDomain(
                            testResult.testUid,
                            testResult.testTitle
                                ?: throw IllegalArgumentException("test title can't be null"),
                            testResult.testDate
                        )
                    }
                    trySend(Result.Success(tests))
                }

            awaitClose {
                callback.remove()
            }

        }
    }

    override suspend fun getCompletedTestQuestions(
        studentUid: String?,
        groupId: String,
        testId: String
    ): Flow<Result<TestResultDomain>> {
        return callbackFlow {

            val listener =
                db.collection("groups")
                    .document(groupId)
                    .collection("tests")
                    .document(testId)
                    .collection("passedStudents").document(studentUid ?: auth.uid!!)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        val result = value?.toObject(TestResult::class.java)?.let { testResult ->
                            TestResultMapper.mapToDomain(
                                testResult
                            )
                        }
                        trySend(Result.Success(result))
                    }
            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.IO)
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
                        title = TestCreationHandler.getNewTest()?.testName,
                        titleKeywords = TestCreationHandler.getNewTest()?.testName?.getKeywords()
                            ?: emptyList()
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
            .orderBy("date", Query.Direction.DESCENDING)
            .whereEqualTo("authorUid", auth.uid)
            .addSnapshotListener { value, error ->
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

    override suspend fun deleteTests(groupId: String, testIds: List<String>): Result<Unit> {
        return try {
            db.runBatch { batch ->
                testIds.forEach { testId ->
                    val ref = db.collection("groups")
                        .document(groupId)
                        .collection("tests")
                        .document(testId)
                    batch.delete(ref)
                }
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateOpenQuestionScore(
        groupId: String,
        testId: String,
        questionId: String,
        studentUid: String,
        newScore: Int
    ): Result<Unit> {
        return try {
            db.collection("groups")
                .document(groupId)
                .collection("tests")
                .document(testId)
                .collection("passedStudents")
                .document(studentUid)
                .update(mapOf("pointsToOpenQuestions.$questionId" to newScore)).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}