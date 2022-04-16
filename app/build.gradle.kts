plugins {
    id("com.mobiletest.app")
    id("com.google.gms.google-services")

}


android {

    hilt {
        enableExperimentalClasspathAggregation = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
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
    implementation(Dependencies.Deps.firebaseCloudMessaging)
    implementation(Dependencies.Deps.hiltWork)
    kapt(Dependencies.Deps.hiltWorkCompiler)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)

}