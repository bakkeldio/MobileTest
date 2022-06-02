package com.edu.mobiletest.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.common.presentation.SingleLiveEvent
import com.edu.mobiletest.data.IAuthRepository
import com.edu.mobiletest.domain.usecase.*
import com.edu.mobiletest.ui.model.ProfileStudentUI
import com.edu.mobiletest.ui.model.ProfileTeacherUI
import com.edu.mobiletest.ui.model.StudentProfileDomainToUIMapper
import com.edu.mobiletest.ui.model.TeacherProfileDomainToUIMapper
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.usecase.GetCompletedTests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getStudentProfileInfo: GetStudentProfileUseCase,
    private val getTeacherProfileInfo: GetTeachersProfileUseCase,
    private val getCompletedTests: GetCompletedTests,
    private val authRepo: IAuthRepository,
    private val uploadToStorage: UploadUserImageToStorage,
    private val deleteAvatar: DeleteProfileAvatarUseCase,
    private val updateProfileImageInDB: UpdateProfileImageInDB,
    private val workManager: WorkManager,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private val _signOut: SingleLiveEvent<ResourceState<Unit>> = SingleLiveEvent()
    val signOut: LiveData<ResourceState<Unit>> = _signOut

    private val _studentProfile: MutableLiveData<ProfileStudentUI> = MutableLiveData()
    val studentProfile: LiveData<ProfileStudentUI> = _studentProfile

    private val _teacherProfile: MutableLiveData<ProfileTeacherUI> = MutableLiveData()
    val teacherProfile: LiveData<ProfileTeacherUI> = _teacherProfile

    private val _completedTests: MutableLiveData<List<PassedTestDomain>> = MutableLiveData()
    val completedTests: LiveData<List<PassedTestDomain>> = _completedTests

    private val _startDownload: MutableLiveData<String?> = MutableLiveData()
    val startToDownload: LiveData<String?> = _startDownload

    fun signOut() {
        workManager.cancelAllWork()
        viewModelScope.launch {
            when (val response = authRepo.signOutUser()) {
                is Result.Success -> {
                    _signOut.value = ResourceState.Success(Unit)
                }
                is Result.Error -> {
                    _signOut.value = ResourceState.Error(response.data?.localizedMessage ?: "")
                }
            }
        }
    }

    fun isUserAdmin() = sharedPreferences.getBoolean("isUserAdmin", false)

    private fun getStudentProfile() {
        showLoader()
        viewModelScope.launch {
            when (val response = getStudentProfileInfo()) {
                is Result.Success -> {
                    _studentProfile.value =
                        response.data?.let {
                            StudentProfileDomainToUIMapper.mapDomainModelToUI(it)
                        }
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }

    fun getProfile() {
        if (sharedPreferences.getBoolean("isUserAdmin", false)) {
            getTeacherProfile()
        } else {
            getStudentProfile()
        }
    }

    private fun getTeacherProfile() {
        showLoader()
        viewModelScope.launch {
            when (val response = getTeacherProfileInfo()) {
                is Result.Success -> {
                    response.data?.let {
                        _teacherProfile.value =
                            TeacherProfileDomainToUIMapper.mapDomainModelToUI(it)
                    }
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            hideLoader()
        }
    }

    fun fetchCompletedTests(groupId: String) {
        viewModelScope.launch {
            getCompletedTests(groupId).collect {
                when (it) {
                    is Result.Success -> {
                        _completedTests.value = it.data ?: emptyList()

                    }
                    is Result.Error -> {
                        _error.value = it.data
                    }
                    else -> Unit
                }
            }
        }
    }

    fun updateProfileImage(url: String?) {
        viewModelScope.launch {
            when (val response = updateProfileImageInDB(url)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun uploadFile(fileUrl: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val response = uploadToStorage(fileUrl)) {
                is Result.Success -> {
                    _startDownload.value = response.data ?: ""
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            _loading.value = false
        }
    }

    fun deleteProfileAvatar() {
        _loading.value = true
        viewModelScope.launch {
            when (val response = deleteAvatar()) {
                is Result.Success -> {
                    _startDownload.value = null
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
            _loading.value = false
        }
    }
}