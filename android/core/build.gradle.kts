plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.example.voiceinput.core"
    compileSdk = 35
    defaultConfig {
        minSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main").java.srcDirs(
            "model/src/main/kotlin",
            "privacy/src/main/kotlin",
            "telemetry/src/main/kotlin",
        )
        getByName("test").java.srcDirs("src/test/kotlin")
    }
}
