package com.edu.test.presentation.workers

import android.app.Activity
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.edu.common.data.model.Test
import com.edu.common.domain.model.StudentInfoDomain
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
class TestTimeWorkerTest {

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var firebaseFirestore: FirebaseFirestore

    @MockK
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var successTask: Task<DocumentSnapshot>
    lateinit var failureTask: Task<DocumentSnapshot>
    lateinit var successVoid: Task<Void>
    lateinit var failureVoid: Task<Void>

    companion object {
        const val TEST_ID = "TEST_ID"
        const val TIME_FOR_TEST = "TIME_FOR_TEST"
        const val TIME = 1
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        successTask = getTaskDocumentSnapshot(true)

        failureTask = getTaskDocumentSnapshot(false)

        successVoid = getTaskVoid(true)

        failureVoid = getTaskVoid(false)
    }


    @org.junit.Test
    fun testTimeWorkerReturnsCorrectOutput() {
        val worker = TestListenableWorkerBuilder<TestTimeWorker>(context)
            .setInputData(workDataOf(TIME_FOR_TEST to TIME, TEST_ID to TEST_ID)).build()
        runBlocking {
            val result = worker.doWork()
            Assert.assertEquals(
                result,
                ListenableWorker.Result.success(workDataOf(TEST_ID to TEST_ID))
            )
        }
    }

    @org.junit.Test
    fun testTimeWorkerReturnsFailureWhenTestIdIsNull() {
        val time = 1
        val worker = TestListenableWorkerBuilder<TestTimeWorker>(context).setInputData(
            workDataOf("TIME_FOR_TEST" to time)
        ).build()
        runBlocking {
            val result = worker.doWork()
            Assert.assertEquals(result, ListenableWorker.Result.failure())
        }
    }

    @org.junit.Test
    fun `successful database updates lead to successful completion of worker `() {
        runBlocking {
            val student = StudentInfoDomain()

            val test = Test()
            every {
                firebaseAuth.uid
            } returns ""


            coEvery {
                firebaseFirestore.collection("students").document(any()).get()
            }.returns(successTask)

            coEvery {
                successTask.await().toObject(StudentInfoDomain::class.java)
            } returns student

            coEvery {
                firebaseFirestore.collection("groups").document(any()).collection("tests")
                    .document(any()).get()
            }.returns(successTask)

            coEvery {
                successTask.await().toObject(Test::class.java)
            } returns test


            coEvery {
                firebaseFirestore.collection("groups")
                    .document(any())
                    .collection("tests")
                    .document(any())
                    .collection("passedStudents")
                    .document(any()).set(any())
            } returns successVoid


            val worker = TestListenableWorkerBuilder<UploadTestResultToFirestoreWorker>(context)
                .setWorkerFactory(Factory(firebaseFirestore, firebaseAuth))
                .setInputData(workDataOf("groupId" to "", "testId" to "")).build()

            Assert.assertEquals(worker.doWork(), ListenableWorker.Result.success())
        }
    }


    class Factory(private val db: FirebaseFirestore, private val auth: FirebaseAuth) :
        WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return UploadTestResultToFirestoreWorker(appContext, workerParameters, db, auth)
        }

    }

    private fun getTaskDocumentSnapshot(isSuccess: Boolean): Task<DocumentSnapshot> {
        val mockK = mockk<DocumentSnapshot>()
        return object : Task<DocumentSnapshot>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
                return this
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<DocumentSnapshot> {
                return this
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<DocumentSnapshot> {
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in DocumentSnapshot>
            ): Task<DocumentSnapshot> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in DocumentSnapshot>
            ): Task<DocumentSnapshot> {
                return this
            }

            override fun getException(): java.lang.Exception? {
                return if (isSuccess){
                    null
                }else{
                    java.lang.Exception(Throwable("Failed"))
                }
            }


            override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot {
                return this.getResult(p0)
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return isSuccessful
            }

            override fun getResult(): DocumentSnapshot {
                return mockK
            }
        }

    }

    private fun getTaskVoid(isSuccess: Boolean): Task<Void> {
        val mockk = mockk<Void>()

        return object : Task<Void>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<Void> {
                return this
            }

            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<Void> {
                return this
            }

            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<Void> {
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in Void>): Task<Void> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(this.result)
                return this
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(this.result)
                return this
            }

            override fun getException(): Exception? {
                return if (isSuccess) {
                    null
                } else {
                    Exception(Throwable("Failed"))
                }
            }

            override fun getResult(): Void {
                return mockk
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): Void {
                return mockk
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return isSuccessful
            }

        }
    }

}