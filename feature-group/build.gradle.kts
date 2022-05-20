plugins {
    id("com.mobiletest.app")
}

android {
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    implementation(Dependencies.Deps.constraintLayout)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}