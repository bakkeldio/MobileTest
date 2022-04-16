package com.edu.mobiletest.domain.usecase

import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteProfileAvatarUseCase @Inject constructor(private val profileRepoImpl: IProfileRepository) {

    suspend operator fun invoke() = profileRepoImpl.deleteProfilePhoto()
}