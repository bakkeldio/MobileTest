package com.edu.test.presentation.question

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.edu.common.data.Result
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.test.domain.usecase.GetTestQuestions
import com.edu.test.domain.usecase.SubmitUserScore
import com.edu.test.presentation.workers.TestTimeWorker
import com.edu.test.presentation.workers.UploadTestResultToFirestoreWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val getQuestionsOfTest: GetTestQuestions,
    private val workManager: WorkManager,
    private val submitUserScore: SubmitUserScore,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    companion object {
        const val TIME_FOR_TEST = "TIME_FOR_TEST"
        const val TEST_ID = "TEST_ID"
        const val UNIQUE_WORK ="UNIQUE_WORK"
    }

    private val _questionsState: MutableLiveData<List<QuestionDomain>> =
        MutableLiveData()

    val questionsState: LiveData<List<QuestionDomain>> get() = _questionsState

    private val _scoreSubmitState: MutableLiveData<ResourceState<Unit>> = MutableLiveData()

    val timerLiveData: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TIMER_WORK)

    fun getAllQuestions(groupId: String, testId: String) {
        showLoader()
        viewModelScope.launch {
            when (val result = getQuestionsOfTest(groupId, testId)) {
                is Result.Success -> {
                    _questionsState.value = result.data ?: emptyList()
                }
                is Result.Error -> {
                    _error.value = result.data
                }
            }
            hideLoader()
        }
    }

    fun submitResultAndCancelWorker(groupId: String, testId: String) {
        workManager.cancelAllWorkByTag(TIMER_WORK)
        workManager.cancelAllWorkByTag(UPLOAD_WORK)
        workManager.pruneWork()

        viewModelScope.launch {
            when (val result = submitUserScore(testId, groupId)) {
                is Result.Success -> {
                    _scoreSubmitState.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _scoreSubmitState.value =
                        ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun startTestTimerWork(
        groupId: String,
        testId: String,
        testTitle: String,
        minutes: Int
    ) {
        sharedPreferences.edit().putString("currentTestId", testId).apply()
        val data = Data.Builder().putInt(TIME_FOR_TEST, minutes).putString(TEST_ID, testId).build()
        val testTimeRequestBuilder = OneTimeWorkRequestBuilder<TestTimeWorker>()
        val uploadResultRequest =
            OneTimeWorkRequestBuilder<UploadTestResultToFirestoreWorker>().addTag(UPLOAD_WORK)
                .setInputData(
                    Data.Builder().putString("groupId", groupId).putString("testId", testId)
                        .putString("testTitle", testTitle).build()
                ).build()


        val testTimeWorkRequest =
            testTimeRequestBuilder.setInputData(data).addTag(TIMER_WORK).build()

        workManager.beginUniqueWork(
            UNIQUE_WORK, ExistingWorkPolicy.KEEP,
            testTimeWorkRequest
        ).then(uploadResultRequest).enqueue()
    }
}