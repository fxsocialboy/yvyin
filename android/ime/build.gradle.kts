import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

val localProperties = Properties().apply {
    val propertiesFile = rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use(::load)
    }
}

fun String.escapeForBuildConfig(): String = replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "com.example.voiceinput.ime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.voiceinput.ime"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        buildConfigField(
            "String",
            "DASHSCOPE_API_KEY",
            "\"${localProperties.getProperty("dashscope.api.key", "").escapeForBuildConfig()}\"",
        )
        buildConfigField("String", "DASHSCOPE_MODEL", "\"qwen3-asr-flash-realtime\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
