plugins {
    id("com.android.library")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    compileSdk  = Dependencies.ConfigData.compileSdk

    defaultConfig {
        minSdk  = Dependencies.ConfigData.minSdk
        targetSdk  = Dependencies.ConfigData.targetSdk
        compileSdk = Dependencies.ConfigData.compileSdk

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.Deps.coreKtx)
    implementation(Dependencies.Deps.appCompat)
    implementation(Dependencies.Deps.constraintLayout)
    implementation(Dependencies.Deps.material)
    implementation(Dependencies.Deps.coroutinesCore)
    implementation(Dependencies.Deps.coroutinesAndroid)
    implementation(Dependencies.Deps.navigationFragmentKtx)
    implementation(Dependencies.Deps.navigationUIKtx)
    implementation(Dependencies.Deps.hiltAndroid)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}