package com.edu.test.presentation.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edu.common.data.model.Test
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.utils.getKeywords
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.data.model.TestResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadTestResultToFirestoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return try {
            val groupId = inputData.getString("groupId") ?: return Result.failure()
            val testId = inputData.getString("testId") ?: return Result.failure()
            val totalScore =
                TestProcessHandler.calculateTotalScore()


            val student =
                db.collection("students").document(firebaseAuth.uid!!).get().await()
                    .toObject(StudentInfoDomain::class.java)
            student ?: return Result.failure()
            val test =
                db.collection("groups").document(groupId).collection("tests").document(testId).get()
                    .await()
                    .toObject(Test::class.java)
            test ?: return Result.failure()

            db.collection("groups")
                .document(groupId)
                .collection("tests")
                .document(testId)
                .collection("passedStudents")
                .document(firebaseAuth.uid!!).set(
                    TestResult(
                        student.uid,
                        student.name,
                        student.avatarUrl,
                        testId,
                        test.title,
                        test.title?.getKeywords() ?: emptyList(),
                        test.date,
                        totalScore,
                        groupId,
                        TestProcessHandler.mapTo(),
                        TestProcessHandler.getAnswersToOpenQuestions()
                    )
                ).await()

            TestProcessHandler.clear()
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}