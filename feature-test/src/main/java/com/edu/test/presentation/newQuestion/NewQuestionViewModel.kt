package com.edu.test.presentation.newQuestion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.usecase.DeleteAnswerFromDbUseCase
import com.edu.test.domain.usecase.GetQuestionAnswersFromDbUseCase
import com.edu.test.domain.usecase.InsertAnswerToDbUseCase
import com.edu.test.domain.usecase.UpdateAnswerInDbUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewQuestionViewModel @Inject constructor(
    private val getQuestionAnswersById: GetQuestionAnswersFromDbUseCase,
    private val updateQuestionAnswer: UpdateAnswerInDbUseCase,
    private val insertQuestionAnswer: InsertAnswerToDbUseCase,
    private val deleteQuestionAnswer: DeleteAnswerFromDbUseCase
) : ViewModel() {

    private val _answers: MutableLiveData<List<QuestionAnswerDomain>> = MutableLiveData()
    val answers: LiveData<List<QuestionAnswerDomain>> = _answers

    fun getQuestionAnswers(id: Int) {
        viewModelScope.launch {
            getQuestionAnswersById(id).collectLatest {
                _answers.value = it
            }
        }
    }

    fun updateAnswer(answerDomain: QuestionAnswerDomain) {
        viewModelScope.launch {
            updateQuestionAnswer(answerDomain)
        }
    }

    fun deleteAnswer(answerDomain: QuestionAnswerDomain) {
        viewModelScope.launch {
            deleteQuestionAnswer(answerDomain)
        }
    }

    fun insertAnswer(answerDomain: QuestionAnswerDomain) {
        viewModelScope.launch {
            insertQuestionAnswer(answerDomain)
        }
    }


}