package com.example.mobiletest.di

import com.example.mobiletest.MainActivity
import com.example.mobiletest.di.module.AppModule
import com.example.mobiletest.ui.LoginActivity
import dagger.Component

@Component(modules = [AppModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(loginActivity: LoginActivity)
}