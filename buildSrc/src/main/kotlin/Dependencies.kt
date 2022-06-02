object Dependencies {
    object Versions {
        const val appCompat = "1.4.0"
        const val material = "1.4.0"
        const val constraintLayout = "2.0.4"

        const val liveData = "2.4.0"
        const val viewModel = "2.4.0"
        const val navigationKtx = "2.4.2"

        const val firebaseAnalytics = "20.0.2"
        const val firebaseAuth = "21.0.1"
        const val firebaseCoroutines = "1.1.1"
        const val firestore = "24.1.0"
        const val firebaseStorage = "20.0.0"
        const val firebaseCloudMessaging = "23.0.2"

        const val coroutinesVersion = "1.6.0"

        const val glideCompiler = "4.13.0"

        const val viewPager2 = "1.0.0"

        const val jUnit = "4.13.2"

        const val work_version = "2.7.1"

        const val fragmentKtx = "1.4.1"

        const val glideVersion = "4.13.0"

        const val edModo = "2.8.0"

        const val room_version = "2.4.2"

        const val firebaseUIStorage = "7.2.0"

        const val kotlinxSerializationJson = "1.3.2"

        const val ktor_version = "2.0.0"

        const val androidJUnit = "1.1.3"
        const val mockito_kotlin = "4.0.0"
        const val espresso = "3.4.0"

        const val hiltVersion = "2.38.1"

        const val hiltWork = "1.0.0"

        const val hiltNavigationVersion = "1.0.0"
        const val gradlePlugin = "7.0.4"
        const val googleServices = "4.3.10"
        const val kotlin = "1.5.21"
        const val coreKtx = "1.7.0"

        const val recyclerView = "1.2.1"

        const val recyclerViewSelection = "1.1.0"
    }

    object ConfigData {
        const val compileSdk = 31
        const val minSdk = 21
        const val targetSdk = 31
        var versionCode = 1
        var versionName = "1.0"
    }

    object BuildPlugins {
        val buildGradle by lazy { "com.android.tools.build:gradle:${Versions.gradlePlugin}" }
        val kotlinGradle by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
        val googleServices by lazy { "com.google.gms:google-services:${Versions.googleServices}" }
        val hiltAndroid by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}" }
        val safeArgs by lazy { "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationKtx}" }
    }

    object Deps {
        val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
        val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
        val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}" }
        val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}" }
        val material by lazy { "com.google.android.material:material:${Versions.material}" }
        val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }
        val liveData by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveData}" }
        val viewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}" }
        val glide by lazy { "com.github.bumptech.glide:glide:${Versions.glideVersion}" }
        val glideCompiler by lazy { "com.github.bumptech.glide:compiler:${Versions.glideCompiler}" }
        val edmodo by lazy { "com.theartofdev.edmodo:android-image-cropper:${Versions.edModo}" }
        val firebaseUIStorage by lazy { "com.firebaseui:firebase-ui-storage:${Versions.firebaseUIStorage}" }
        val fragmentKtx by lazy { "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}" }
        val navigationFragmentKtx by lazy { "androidx.navigation:navigation-fragment-ktx:${Versions.navigationKtx}" }
        val navigationUIKtx by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}" }
        val viewPager2 by lazy { "androidx.viewpager2:viewpager2:${Versions.viewPager2}" }
        val recyclerView by lazy { "androidx.recyclerview:recyclerview:${Versions.recyclerView}" }
        val firebaseAnalytics by lazy { "com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalytics}" }
        val firebaseAuth by lazy { "com.google.firebase:firebase-auth-ktx:${Versions.firebaseAuth}" }
        val firebaseFirestore by lazy { "com.google.firebase:firebase-firestore-ktx:${Versions.firestore}" }
        val firebaseStorage by lazy { "com.google.firebase:firebase-storage-ktx:${Versions.firebaseStorage}" }
        val firebaseCloudMessaging by lazy { "com.google.firebase:firebase-messaging-ktx:${Versions.firebaseCloudMessaging}" }
        val firebaseCoroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.firebaseCoroutines}" }
        val hiltAndroid by lazy { "com.google.dagger:hilt-android:${Versions.hiltVersion}" }
        val hiltAndroidCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}" }
        val hiltNavigation by lazy { "androidx.hilt:hilt-navigation-fragment:${Versions.hiltNavigationVersion}" }
        val workManager by lazy { "androidx.work:work-runtime-ktx:${Versions.work_version}" }
        val roomRuntime by lazy { "androidx.room:room-runtime:${Versions.room_version}" }
        val roomCompiler by lazy {
            "androidx.room:room-compiler:${Versions.room_version}"
        }
        val roomKtx by lazy {
            "androidx.room:room-ktx:${Versions.room_version}"
        }
        val kotlinXSerializationJson by lazy {
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationJson}"
        }
        val ktor by lazy {
            "io.ktor:ktor-client-android:${Versions.ktor_version}"
        }
        val ktorLogging by lazy {
            "io.ktor:ktor-client-logging:${Versions.ktor_version}"
        }
        val recyclerViewSelection by lazy { "androidx.recyclerview:recyclerview-selection:${Versions.recyclerViewSelection}" }
        val hiltWork by lazy { "androidx.hilt:hilt-work:${Versions.hiltWork}" }
        val hiltWorkCompiler by lazy { "androidx.hilt:hilt-compiler:${Versions.hiltWork}" }
        val jUnit by lazy { "junit:junit:${Versions.jUnit}" }
        val mockito_kotlin by lazy { "org.mockito.kotlin:mockito-kotlin:${Versions.mockito_kotlin}" }
        val androidJUnit by lazy { "androidx.test.ext:junit:${Versions.androidJUnit}" }
        val espresso by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
        val workTesting by lazy { "androidx.work:work-testing:${Versions.work_version}" }
    }
}