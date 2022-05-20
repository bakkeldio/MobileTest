package com.edu.mobiletestadmin.data.di

import com.edu.mobiletestadmin.data.repository.*
import com.edu.mobiletestadmin.presentation.viewModel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<IUsersRepo> { UsersRepoImpl(get(), get()) }
    single<IAuthRepo> { AuthRepoImpl(get(), get()) }
    single<IGroupRepo> { GroupRepoImpl(get(), get()) }
    single { FirebaseStorage.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFunctions.getInstance("asia-south1") }
}