
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("kapt")

}

android {
    compileSdk = Dependencies.ConfigData.compileSdk

    defaultConfig {
        applicationId = "com.example.mobiletest"
        minSdk = Dependencies.ConfigData.minSdk
        targetSdk = Dependencies.ConfigData.targetSdk
        versionCode  = Dependencies.ConfigData.versionCode
        versionName = Dependencies.ConfigData.versionName

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-files.pro")
        }
    }
    compileOptions {
        sourceCompatibility  = JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding  = true
    }

}

dependencies {
    implementation(project(":test"))
    implementation(project(":chat"))
    implementation(project(":core"))
    implementation(project(":group"))
    implementation(Dependencies.Deps.constraintLayout)
    implementation(Dependencies.Deps.liveData)
    implementation(Dependencies.Deps.viewModel)
    implementation(Dependencies.Deps.firebaseAnalytics)
    implementation(Dependencies.Deps.hiltAndroid)
    implementation(Dependencies.Deps.hiltWork)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    kapt(Dependencies.Deps.hiltWorkCompiler)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)

}