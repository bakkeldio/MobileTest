package com.edu.mobiletest.domain.usecase

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UploadUserImageToStorage @Inject constructor(private val profileRepository: IProfileRepository) {

    suspend operator fun invoke(fileUrl: String): Result<String> {
        return profileRepository.uploadProfilePhoto(fileUrl)
    }
}