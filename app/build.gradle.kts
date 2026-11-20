plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "id.sch.smkn6muarojambi.exambrowser"
    compileSdk = 35

    defaultConfig {
        applicationId = "id.sch.smkn6muarojambi.exambrowser"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Tanda tangan release sengaja tidak disertakan.
            // Lihat README.md bagian "Membuat APK release".
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Hanya Kotlin stdlib yang ditambahkan secara otomatis oleh plugin Kotlin.
    // Tidak ada framework UI, database, atau jaringan tambahan.
}
