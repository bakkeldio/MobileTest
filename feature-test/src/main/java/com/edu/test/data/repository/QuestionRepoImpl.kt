package com.edu.test.data.repository

import com.edu.common.domain.Result
import com.edu.common.domain.model.QuestionDomain
import com.edu.test.data.mapper.QuestionMapperImpl
import com.edu.test.data.model.Question
import com.edu.test.domain.repository.IQuestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepoImpl @Inject constructor(private val db: FirebaseFirestore) :
    IQuestionRepository {

    override suspend fun getQuestionsOfTest(
        testUid: String,
        groupId: String
    ): Result<List<QuestionDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    db.collection("groups").document(groupId).collection("tests").document(testUid)
                        .collection("questions").get().await()
                val list = response.documents.map {
                    val model = it.toObject(Question::class.java)
                        ?: throw IllegalArgumentException("Question model can't be null")
                    QuestionMapperImpl.mapToDomain(model, it.id)
                }
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}