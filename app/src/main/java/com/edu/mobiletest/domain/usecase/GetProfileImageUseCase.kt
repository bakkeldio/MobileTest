package com.edu.mobiletest.domain.usecase

import com.edu.common.domain.Result
import com.edu.mobiletest.domain.repository.IProfileRepository
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetProfileImageUseCase @Inject constructor(private val profileRepo: IProfileRepository) {

    operator fun invoke() = profileRepo.getProfileImage().catch {
        emit(Result.Error(it))
    }

}