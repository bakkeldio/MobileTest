package com.edu.mobiletest.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.edu.common.data.Result
import com.edu.common.domain.model.TeacherProfile
import com.edu.common.domain.model.TestDomainModel
import com.edu.common.presentation.BaseViewModel
import com.edu.common.presentation.ResourceState
import com.edu.common.presentation.SingleLiveEvent
import com.edu.mobiletest.data.IAuthRepository
import com.edu.mobiletest.domain.model.StudentProfileComposed
import com.edu.mobiletest.domain.usecase.DeleteProfileAvatarUseCase
import com.edu.mobiletest.domain.usecase.ProfileInfoUseCase
import com.edu.mobiletest.domain.usecase.UpdateProfileImageInDB
import com.edu.mobiletest.domain.usecase.UploadUserImageToStorage
import com.edu.mobiletest.ui.model.ProfileStudentUI
import com.edu.mobiletest.ui.model.ProfileTeacherUI
import com.edu.mobiletest.ui.model.StudentProfileDomainToUIMapper
import com.edu.mobiletest.ui.model.TeacherProfileDomainToUIMapper
import com.edu.test.domain.model.PassedTestDomain
import com.edu.test.domain.model.TestsListState
import com.edu.test.domain.usecase.GetCompletedTests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileInfo: ProfileInfoUseCase,
    private val getCompletedTests: GetCompletedTests,
    private val authRepo: IAuthRepository,
    private val uploadToStorage: UploadUserImageToStorage,
    private val deleteAvatar: DeleteProfileAvatarUseCase,
    private val updateProfileImageInDB: UpdateProfileImageInDB,
    private val workManager: WorkManager
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

    fun getProfile() {
        showLoader()
        viewModelScope.launch {
            when (val response = getProfileInfo()) {
                is Result.Success -> {
                    response.data?.let { profile ->
                        if (profile is StudentProfileComposed) {
                            _studentProfile.value =
                                StudentProfileDomainToUIMapper.mapDomainModelToUI(
                                    profile
                                )
                        } else if (profile is TeacherProfile) {
                            _teacherProfile.value =
                                TeacherProfileDomainToUIMapper.mapDomainModelToUI(profile)
                        }
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