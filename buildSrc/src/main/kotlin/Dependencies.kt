object Dependencies {
    object Versions {
        const val appCompat = "1.4.0"
        const val material = "1.4.0"
        const val constraintLayout = "2.0.4"

        const val liveData = "2.4.0"
        const val viewModel = "2.4.0"
        const val navigationKtx = "2.3.5"

        const val firebaseAnalytics = "20.0.2"
        const val firebaseAuth = "21.0.1"
        const val firebaseCoroutines = "1.1.1"

        const val jUnit = "4.13.2"

        const val androidJUnit = "1.1.3"
        const val espresso = "3.4.0"

        const val daggerVersion = "2.40.5"
        const val hiltVersion = "2.38.1"

        const val gradlePlugin = "7.0.4"
        const val googleServices = "4.3.10"
        const val kotlin = "1.5.0"
    }

    object ConfigData{
        const val compileSdk = 31
        const val minSdk = 21
        const val targetSdk = 32
        const val versionCode = 1
        const val versionName = "1.0"
    }

    object BuildPlugins {
        val buildGradle by lazy { "com.android.tools.build:gradle:${Versions.gradlePlugin}" }
        val kotlinGradle by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
        val googleServices by lazy { "com.google.gms:google-services:${Versions.googleServices}" }
        val hiltAndroid by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}" }
    }

    object Deps {
        val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
        val coreKtx by lazy { "androidx.core:core-ktx:${Versions.kotlin}" }
        val material by lazy { "com.google.android.material:material:${Versions.material}" }
        val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }
        val liveData by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveData}" }
        val viewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}" }
        val navigationFragmentKtx by lazy { "androidx.navigation:navigation-fragment-ktx:${Versions.navigationKtx}" }
        val navigationUIKtx by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}" }
        val firebaseAnalytics by lazy { "com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalytics}" }
        val firebaseAuth by lazy { "com.google.firebase:firebase-auth-ktx:${Versions.firebaseAuth}"}
        val firebaseCoroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.firebaseCoroutines}" }
        val hiltAndroid by lazy { "com.google.dagger:hilt-android:${Versions.hiltVersion}" }
        val hiltAndroidCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}" }
        val jUnit by lazy { "junit:junit:${Versions.jUnit}" }
        val androidJUnit by lazy { "androidx.test.ext:junit:${Versions.androidJUnit}" }
        val espresso by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
    }
}