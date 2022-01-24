// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.BuildPlugins.buildGradle)
        classpath(Dependencies.BuildPlugins.kotlinGradle)
        classpath(Dependencies.BuildPlugins.googleServices)
        classpath(Dependencies.BuildPlugins.hiltAndroid)
        classpath(Dependencies.BuildPlugins.safeArgs)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}