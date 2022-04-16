import Dependencies.Versions.gradlePlugin

plugins {
    id("com.mobiletest.app")
}

android {
    buildFeatures {
        viewBinding = true
    }

    hilt {
        enableAggregatingTask = true
    }
}


dependencies {
    implementation(project(":core"))

    implementation(Dependencies.Deps.recyclerView)
    testImplementation(Dependencies.Deps.jUnit)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}