pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "voice-input-mvp"

include(":ime")
include(":core")

project(":ime").projectDir = file("android/ime")
project(":core").projectDir = file("android/core")

