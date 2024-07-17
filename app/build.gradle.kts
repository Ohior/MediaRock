plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("io.objectbox") // Apply last.
}

android {
    namespace = "ohior.app.mediarock"
    compileSdk = 34

    defaultConfig {
        applicationId = "ohior.app.mediarock"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)
//    splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    //    navigation
    implementation(libs.androidx.navigation.compose) // Add this line
//    icons more
    implementation(libs.androidx.material) // Add this line
//    scraping
    implementation(libs.okhttp)
    implementation(libs.jsoup)
//    image display
    implementation(libs.coil.compose)
//    serializable
    implementation(libs.kotlinx.serialization.json)
//    video player
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
//    lottie
    implementation(libs.lottie.compose)
    // pdf viewer
    implementation(libs.bouquet)
    // ktor client
    implementation("io.ktor:ktor-client-core:2.3.2")
    implementation("io.ktor:ktor-client-cio:2.3.2") // or ktor-client-okhttp, ktor-client-jetty, etc.
    // font
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")
    // datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}