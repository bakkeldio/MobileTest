plugins {
    id("com.mobiletest.app")
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    hilt {
        enableAggregatingTask = true
    }
}


dependencies {
    implementation(project(":core"))
    implementation(Dependencies.Deps.hiltWork)
    implementation(Dependencies.Deps.roomRuntime)
    implementation(Dependencies.Deps.kotlinXSerializationJson)
    implementation(Dependencies.Deps.ktor)
    implementation(Dependencies.Deps.ktorLogging)
    implementation(Dependencies.Deps.roomKtx)
    implementation(Dependencies.Deps.recyclerViewSelection)
    implementation(Dependencies.Deps.recyclerView)
    kapt(Dependencies.Deps.roomCompiler)
    kapt(Dependencies.Deps.hiltWorkCompiler)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}