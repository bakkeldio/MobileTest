package com.edu.mobiletestadmin.presentation.app

import android.app.Application
import com.edu.mobiletestadmin.data.di.dataModule
import com.edu.mobiletestadmin.presentation.di.presentationModules
import com.edu.mobiletestadmin.presentation.di.viewModuleModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AdminApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AdminApplication)
            modules(dataModule, presentationModules, viewModuleModules)
        }
    }
}