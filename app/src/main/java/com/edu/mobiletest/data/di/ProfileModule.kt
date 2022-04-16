package com.edu.mobiletest.data.di

import com.edu.common.domain.repository.ITeachersRepo
import com.edu.mobiletest.data.ProfileRepoImpl
import com.edu.mobiletest.data.TeachersRepoImpl
import com.edu.mobiletest.domain.repository.IProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface ProfileModule {

    @Binds
    fun bindProfileRepo(profileRepoImpl: ProfileRepoImpl): IProfileRepository

    @Binds
    fun bindTeachersRepo(teachersRepoImpl: TeachersRepoImpl): ITeachersRepo

}