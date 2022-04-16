package com.edu.mobiletest.domain.repository

import com.edu.common.data.Result
import com.edu.common.domain.model.IProfile
import com.edu.mobiletest.domain.model.NewUserData
import com.edu.mobiletest.domain.model.ProfileType

interface IProfileRepository {
    suspend fun getProfileInfo(): Result<Pair<ProfileType, IProfile>>

    suspend fun uploadProfilePhoto(url: String): Result<String>

    suspend fun deleteProfilePhoto(): Result<Unit>

    suspend fun updateProfileImageDB(url: String?): Result<Unit>

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit)

    fun updateFCMRegistrationTokens(registrationTokens: MutableList<String>)

    fun checkIfUserIsSignedIn(): Boolean

    suspend fun getCurrentRegistrationToken(): Result<String>

}