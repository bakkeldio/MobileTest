package com.edu.test.presentation.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TestDomainModel
import com.edu.test.data.datamanager.TestProcessHandler
import com.edu.test.data.model.TestResult
import com.edu.test.presentation.question.QuestionsViewModel
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


            db.runTransaction { transaction ->
                val student =
                    transaction.get(db.collection("students").document(firebaseAuth.uid!!))
                        .toObject(StudentInfoDomain::class.java) ?: StudentInfoDomain()
                val test = transaction.get(
                    db.collection("groups").document(groupId).collection("tests").document(testId)
                ).toObject(TestDomainModel::class.java) ?: return@runTransaction
                transaction.set(
                    db.collection("groups")
                        .document(groupId)
                        .collection("tests")
                        .document(testId)
                        .collection("passedStudents")
                        .document(firebaseAuth.uid!!),
                    TestResult(
                        student.uid,
                        student.name,
                        student.avatarUrl,
                        testId,
                        test.title,
                        test.date,
                        totalScore,
                        TestProcessHandler.mapTo(),
                        TestProcessHandler.getAnswersToOpenQuestions()
                    )
                )
                transaction.update(
                    db.collection("students").document(firebaseAuth.uid!!),
                    "overallScore",
                    FieldValue.increment(totalScore.toDouble())
                )
            }.await()

            TestProcessHandler.clear()
            Result.success(
                workDataOf(
                    QuestionsViewModel.TEST_ID to inputData.getString(
                        QuestionsViewModel.TEST_ID
                    )
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}