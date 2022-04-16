plugins {
    id("com.mobiletest.app")
    id("kotlin-parcelize")
}
android {
    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }
}
dependencies {

    implementation(project(":core"))
    implementation(Dependencies.Deps.fragmentKtx)
    implementation(Dependencies.Deps.hiltWork)
    implementation(Dependencies.Deps.recyclerViewSelection)
    kapt(Dependencies.Deps.hiltWorkCompiler)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    implementation(Dependencies.Deps.viewPager2)
    testImplementation(Dependencies.Deps.jUnit)
    androidTestImplementation(Dependencies.Deps.androidJUnit)
    androidTestImplementation(Dependencies.Deps.espresso)
}