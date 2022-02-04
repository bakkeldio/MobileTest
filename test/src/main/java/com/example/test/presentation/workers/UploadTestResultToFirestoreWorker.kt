package com.example.test.presentation.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.common.data.test.TestProcessHandler
import com.example.common.data.test.model.TestResult
import com.example.test.presentation.QuestionsViewModel
import com.google.firebase.auth.FirebaseAuth
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
            val testTitle = inputData.getString("testTitle") ?: return Result.failure()
            val totalScore =
                TestProcessHandler.calculateTotalScore()

            db.collection("groups").document(groupId).collection("students")
                .document(firebaseAuth.uid!!).collection("tests").document(testId)
                .set(TestResult(testId, testTitle, totalScore, TestProcessHandler.mapTo())).await()

            TestProcessHandler.clear()
            Result.success(workDataOf(QuestionsViewModel.TEST_ID to inputData.getString(QuestionsViewModel.TEST_ID)))
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}