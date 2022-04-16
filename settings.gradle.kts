dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }// Warning: this repository is going to shut down soon
    }
}
rootProject.name = "MobileTest"
include(":app")
include(":test")
include(":core")
include(":chat")
include(":group")
