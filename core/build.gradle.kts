plugins {
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = Dependencies.ConfigData.compileSdk

    defaultConfig {
        minSdk = Dependencies.ConfigData.minSdk
        targetSdk = Dependencies.ConfigData.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-files.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    api(Dependencies.Deps.coreKtx)
    api(Dependencies.Deps.appCompat)
    api(Dependencies.Deps.material)
    api(Dependencies.Deps.coroutinesCore)
    api(Dependencies.Deps.coroutinesAndroid)
    api(Dependencies.Deps.firebaseFirestore)
    api(Dependencies.Deps.firebaseCoroutines)
    api(Dependencies.Deps.firebaseAuth)
    api(Dependencies.Deps.navigationUIKtx)
    api(Dependencies.Deps.navigationFragmentKtx)
    api(Dependencies.Deps.workManager)
    implementation(Dependencies.Deps.hiltAndroid)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
}