import com.android.build.gradle.internal.utils.findKaptConfigurationsForVariant

plugins {
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Dependencies.ConfigData.compileSdk

    defaultConfig {
        minSdk = Dependencies.ConfigData.minSdk
        targetSdk = Dependencies.ConfigData.targetSdk

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled  = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-files.pro")
        }
    }
    compileOptions {
        sourceCompatibility  = JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(Dependencies.Deps.coreKtx)
    implementation(Dependencies.Deps.appCompat)
    implementation(Dependencies.Deps.material)
    implementation(Dependencies.Deps.coroutinesCore)
    implementation(Dependencies.Deps.firebaseFirestore)
    implementation(Dependencies.Deps.firebaseCoroutines)
    implementation(Dependencies.Deps.firebaseAuth)
    implementation(Dependencies.Deps.hiltAndroid)
    implementation("androidx.navigation:navigation-common-ktx:2.3.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
}