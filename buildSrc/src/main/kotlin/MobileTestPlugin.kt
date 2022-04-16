import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class MobileTestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configurePlugins()
        target.configureAndroid()
        target.configureDependencies()
    }
}

internal fun Project.configureAndroid() = this.extensions.getByType<BaseExtension>().run {
    compileSdkVersion(Dependencies.ConfigData.compileSdk)
    defaultConfig {
        minSdk = Dependencies.ConfigData.minSdk
        targetSdk = Dependencies.ConfigData.targetSdk
        versionName = Dependencies.ConfigData.versionName
        versionCode = Dependencies.ConfigData.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-files.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

internal fun Project.configureDependencies() = dependencies {
    add("implementation", Dependencies.Deps.coreKtx)
    add("implementation", Dependencies.Deps.appCompat)
    add("implementation", Dependencies.Deps.material)
    add("implementation", Dependencies.Deps.coroutinesCore)
    add("implementation", Dependencies.Deps.coroutinesAndroid)
    add("implementation", Dependencies.Deps.firebaseFirestore)
    add("implementation", Dependencies.Deps.firebaseCoroutines)
    add("implementation", Dependencies.Deps.firebaseAuth)
    add("implementation", Dependencies.Deps.navigationUIKtx)
    add("implementation", Dependencies.Deps.navigationFragmentKtx)
    add("implementation", Dependencies.Deps.workManager)
    add("implementation", Dependencies.Deps.hiltAndroid)
}

internal fun Project.configurePlugins() {
    if (this.name == "app") {
        plugins.apply("com.android.application")
    }else{
        plugins.apply("com.android.library")
    }
    plugins.apply("kotlin-android")
    plugins.apply("dagger.hilt.android.plugin")
    plugins.apply("kotlin-kapt")
    plugins.apply("androidx.navigation.safeargs.kotlin")
}




