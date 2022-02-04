package com.example.common.data.test

import com.example.common.data.Result
import com.example.common.data.test.model.Question
import com.example.common.data.test.model.mapper.QuestionMapperImpl
import com.example.common.domain.test.model.QuestionDomain
import com.example.common.domain.test.repository.IQuestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityRetainedScoped
class QuestionRepoImpl @Inject constructor(private val db: FirebaseFirestore) : IQuestionRepository {

    override suspend fun getQuestionsOfTest(
        testUid: String,
        groupId: String
    ): Result<List<QuestionDomain>> {
        return try {
            val response =
                db.collection("groups").document(groupId).collection("tests").document(testUid)
                    .collection("questions").get().await()
            val list = response.documents.mapNotNull {
                it.toObject(Question::class.java)
            }.map {
                QuestionMapperImpl.mapToDomain(it)
            }
            Result.Success(list)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}