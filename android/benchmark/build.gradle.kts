plugins {
    id("com.android.test")
    kotlin("android")
}

android {
    namespace = "com.example.voiceinput.benchmark"
    compileSdk = 35
    defaultConfig {
        minSdk = 31
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

