plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk  = Dependencies.ConfigData.compileSdk

    defaultConfig {
        minSdk = Dependencies.ConfigData.minSdk
        targetSdk = Dependencies.ConfigData.targetSdk

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.Deps.coreKtx)
    implementation(Dependencies.Deps.appCompat)
    implementation(Dependencies.Deps.material)
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}