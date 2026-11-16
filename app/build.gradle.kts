
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vivek.aikeyboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vivek.aikeyboard"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // Use Java 17 for Java compilation
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Use Java 17 for Kotlin compilation
    kotlinOptions {
        jvmTarget = "17"
    }
}
