package com.example.test.presentation

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.common.data.Result
import com.example.common.domain.test.model.QuestionDomain
import com.example.common.presentation.ResourceState
import com.example.test.domain.usecase.GetTestQuestions
import com.example.test.domain.usecase.SubmitUserScore
import com.example.test.presentation.workers.TestTimeWorker
import com.example.test.presentation.workers.UploadTestResultToFirestoreWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class QuestionsViewModel @Inject constructor(
    private val getQuestionsOfTest: GetTestQuestions,
    private val workManager: WorkManager,
    private val submitUserScore: SubmitUserScore,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        const val TIME_FOR_TEST = "TIME_FOR_TEST"
        const val UNIQUE_WORK_CHAIN_NAME = "UNIQUE"
        const val UPLOAD_WORK = "UPLOAD_WORK"
        const val TIMER_WORK = "TIMER_WORK"
        const val TEST_ID = "TEST_ID"
    }

    private val _questionsState: MutableLiveData<ResourceState<List<QuestionDomain>>> =
        MutableLiveData()

    val questionsState: LiveData<ResourceState<List<QuestionDomain>>> get() = _questionsState

    private val _scoreSubmitState: MutableLiveData<ResourceState<Unit>> = MutableLiveData()

    fun getAllQuestions(groupId: String, testId: String) {
        _questionsState.value = ResourceState.Loading
        viewModelScope.launch {
            when (val result = getQuestionsOfTest(groupId, testId)) {
                is Result.Success -> {
                    _questionsState.value = ResourceState.Success(result.data ?: emptyList())
                }
                is Result.Error -> {
                    _questionsState.value = ResourceState.Error(result.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun submitResultAndCancelWorker(groupId: String, testId: String, testTitle: String) {
        workManager.cancelUniqueWork(UNIQUE_WORK_CHAIN_NAME)
        viewModelScope.launch {
            when (val result = submitUserScore(testId, groupId, testTitle)) {
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
    ): LiveData<List<WorkInfo>> {
        sharedPreferences.edit().putString("currentTestId", testId).apply()
        val data = Data.Builder().putInt(TIME_FOR_TEST, minutes).putString(TEST_ID, testId).build()
        val testTimeRequestBuilder = OneTimeWorkRequestBuilder<TestTimeWorker>().setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        val uploadResultRequest =
            OneTimeWorkRequestBuilder<UploadTestResultToFirestoreWorker>().setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).addTag(UPLOAD_WORK).setInputData(
                Data.Builder().putString("groupId", groupId).putString("testId", testId)
                    .putString("testTitle", testTitle).build()
            ).build()


        val testTimeWorkRequest = testTimeRequestBuilder.setInputData(data).addTag(TIMER_WORK).build()

        workManager.beginUniqueWork(
            UNIQUE_WORK_CHAIN_NAME,
            ExistingWorkPolicy.KEEP,
            testTimeWorkRequest
        ).then(uploadResultRequest).enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_CHAIN_NAME)
    }
}