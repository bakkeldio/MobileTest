package com.edu.mobiletest.data.di

import com.edu.mobiletest.data.AuthRepositoryImpl
import com.edu.mobiletest.data.IAuthRepository
import com.edu.mobiletest.data.IRolesRepository
import com.edu.mobiletest.data.RolesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): IAuthRepository

    @Binds
    abstract fun bindRolesRepository(rolesRepository: RolesRepositoryImpl): IRolesRepository

}