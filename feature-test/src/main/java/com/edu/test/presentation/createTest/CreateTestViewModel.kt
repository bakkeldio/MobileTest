package com.edu.test.presentation.createTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.common.domain.Result
import com.edu.common.domain.model.QuestionType
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.common.presentation.SingleLiveEvent
import com.edu.test.domain.model.CreateQuestionDomain
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class CreateTestViewModel @Inject constructor(
    private val uploadTest: CreateTestUseCase,
    private val saveTestInDbUseCase: SaveTestInDbUseCase,
    private val getSavedTestInfo: GetTestInfoUseCase,
    private val updateTestInDbUseCase: UpdateTestInDbUseCase,
    private val getTestQuestionsFromDb: GetTestQuestionsFromDbUseCase,
    private val createQuestionInDb: CreateQuestionInDbUseCase,
    private val updateQuestionInDb: UpdateQuestionInDbUseCase,
    private val deleteQuestionByUid: DeleteQuestionByUidFromDbUseCase,
    private val deleteTestByUidFromDb: DeleteTestFromDbUseCase
) :
    BaseViewModel() {

    private val _createTest: SingleLiveEvent<ResourceState<Unit>> = SingleLiveEvent()
    val createTestState: LiveData<ResourceState<Unit>> = _createTest

    private val _savedTest: MutableLiveData<TestDomain> = MutableLiveData()
    val savedTest: LiveData<TestDomain> = _savedTest

    private val _questions: MutableLiveData<List<QuestionDomain>> = MutableLiveData()
    val questions: LiveData<List<QuestionDomain>> = _questions

    fun uploadNewTest(groupId: String, testId: String) {
        _createTest.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = uploadTest(groupId, testId)) {
                is Result.Success -> {
                    _createTest.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _createTest.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun saveTestInDb(
        groupId: String,
        id: String,
        testName: String,
        testDate: Date,
        testDuration: Int
    ) {
        viewModelScope.launch {
            _savedTest.value = saveTestInDbUseCase(
                TestDomain(
                    id,
                    testName,
                    testDate,
                    testDuration,
                    groupId
                )
            )
        }
    }

    fun getSavedTestQuestions(testUid: String) {
        viewModelScope.launch {
            getTestQuestionsFromDb(testUid).collectLatest {
                _questions.value = it
            }
        }
    }

    fun updateTestInDb(
        id: String,
        testName: String,
        testDate: Date,
        testDuration: Int,
        groupUid: String
    ) {
        viewModelScope.launch {
            updateTestInDbUseCase(TestDomain(id, testName, testDate, testDuration, groupUid))
        }
    }

    fun getTestInfo(uid: String) {
        viewModelScope.launch {
            _savedTest.value = getSavedTestInfo(uid)
        }
    }

    fun createQuestion(testUid: String) {
        viewModelScope.launch {
            createQuestionInDb(testUid)
        }
    }

    fun updateQuestion(question: CreateQuestionDomain, testId: String) {
        viewModelScope.launch {
            updateQuestionInDb(
                QuestionWithAnswersDomain(
                    QuestionDomain(
                        question.id,
                        question.question,
                        question.point,
                        QuestionType.getByValue(question.questionType),
                        testId
                    )
                )
            )
        }
    }

    fun deleteQuestion(questionId: Int) {
        viewModelScope.launch {
            deleteQuestionByUid(questionId)
        }
    }

    fun deleteTestFromDb() {
        viewModelScope.launch {
            savedTest.value?.let { test ->
                deleteTestByUidFromDb(test)
            }
            _savedTest.value = null
        }
    }

}