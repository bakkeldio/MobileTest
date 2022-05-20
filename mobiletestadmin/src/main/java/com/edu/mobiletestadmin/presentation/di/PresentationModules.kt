package com.edu.mobiletestadmin.presentation.di

import com.edu.mobiletestadmin.presentation.viewModel.*
import com.edu.mobiletestadmin.utils.GlideImageLoaderImpl
import com.edu.mobiletestadmin.utils.IImageLoader
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModuleModules = module {
    viewModel { UsersViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { GroupViewModel(get()) }
}

val presentationModules = module {
    single<IImageLoader> { GlideImageLoaderImpl(get()) }
}