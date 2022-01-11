package com.example.mobiletest.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideFirebaseUser(): FirebaseAuth = Firebase.auth
}