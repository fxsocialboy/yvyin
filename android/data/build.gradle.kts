plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.example.voiceinput.data"
    compileSdk = 35
    defaultConfig {
        minSdk = 31
    }
}

dependencies {
    implementation(project(":core"))
}

