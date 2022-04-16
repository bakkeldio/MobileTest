package com.edu.mobiletest.domain.usecase

import com.edu.common.data.Result
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateProfileImageInDB @Inject constructor(private val profileRepo: IProfileRepository) {

    suspend operator fun invoke(url: String?): Result<Unit>{
        return profileRepo.updateProfileImageDB(url)
    }
}