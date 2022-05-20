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
include(":feature-test")
include(":core")
include(":feature-chat")
include(":feature-group")
include(":mobiletestadmin")
