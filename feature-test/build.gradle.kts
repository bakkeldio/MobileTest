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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
    testImplementation("androidx.work:work-testing:2.7.1")
    testImplementation("org.robolectric:robolectric:4.8")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("io.mockk:mockk:1.12.4")

    androidTestImplementation(Dependencies.Deps.espresso)
}