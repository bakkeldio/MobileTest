plugins {
    id("com.mobiletest.app")
    id("kotlin-parcelize")
}

android {
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    api(Dependencies.Deps.edmodo)
    api(Dependencies.Deps.firebaseStorage)
    api(Dependencies.Deps.firebaseUIStorage)
    implementation("androidx.test.ext:junit-ktx:1.1.3")
    annotationProcessor(Dependencies.Deps.glideCompiler)
    implementation(Dependencies.Deps.glide)
    kapt(Dependencies.Deps.hiltAndroidCompiler)
    testImplementation(Dependencies.Deps.jUnit)
}