package com.example.mobiletest.data.di

import com.example.mobiletest.data.AuthRepositoryImpl
import com.example.mobiletest.data.IAuthRepository
import com.example.mobiletest.data.IRolesRepository
import com.example.mobiletest.data.RolesRepositoryImpl
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