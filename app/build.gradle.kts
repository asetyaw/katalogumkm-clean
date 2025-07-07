plugins {
    alias(libs.plugins.kotlin.android)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.katalogumkm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.katalogumkm"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    signingConfigs {
//        getByName("debug") {
//            storeFile = file("D:\\Kehidupan\\KULIAH\\Semester 4\\Pemrograman Mobile 1\\katalogumkm\\katalogumkm.jks")
//            keyAlias = "key0"
//            keyPassword = "aasetya"
//            storePassword = "aasetya"
//        }
//
//        create("release") {
//            storeFile = file("D:\\Kehidupan\\KULIAH\\Semester 4\\Pemrograman Mobile 1\\katalogumkm\\katalogumkm.jks")
//            keyAlias = "key0"
//            keyPassword = "aasetya"
//            storePassword = "aasetya"
//        }
//    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.google.firebase:firebase-appcheck-debug:17.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.4.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
