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
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

}
dependencies {

    implementation(project(":core"))
    implementation(Dependencies.Deps.fragmentKtx)
    implementation(Dependencies.Deps.hiltWork)
    implementation(Dependencies.Deps.recyclerViewSelection)
    implementation(Dependencies.Deps.roomRuntime)
    implementation(Dependencies.Deps.roomKtx)
    implementation(Dependencies.Deps.kotlinXSerializationJson)
    implementation(Dependencies.Deps.viewPager2)
    implementation(Dependencies.Deps.hiltNavigation)
    kapt(Dependencies.Deps.roomCompiler)
    kapt(Dependencies.Deps.hiltWorkCompiler)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
    testImplementation("androidx.work:work-testing:2.7.1")
    testImplementation("org.robolectric:robolectric:4.8")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("io.mockk:mockk:1.12.4")

    androidTestImplementation(Dependencies.Deps.espresso)
}