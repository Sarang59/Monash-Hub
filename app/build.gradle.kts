import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.loginpage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.loginpage"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // For Sending email
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {

    // Firebase BOM
    implementation(platform(libs.firebase.bom.v3272))
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.firebase.analytics.ktx)

    implementation(libs.androidx.activity.ktx)

    // For graph
    implementation(libs.mpandroidchart)

    // Google Sign In
    implementation(libs.play.services.auth)

    // Coroutines
    implementation(libs.kotlinx.coroutines.play.services)

    // Gson
    implementation(libs.gson)

    // email dependencies
    implementation(libs.mail.android.mail)
    implementation(libs.android.activation)

    // Google Drive API
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.client.gson.v1341)
    implementation(libs.google.http.client.android)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.services.drive.vv3rev20230815200)

    // Coil for image loading
    implementation(libs.coil.compose.v222)

    // AndroidX and Compose
    implementation(libs.material3)
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.androidx.ui.v150)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material3.v110)
    implementation(libs.androidx.navigation.compose.v260)
    implementation(libs.androidx.material3.v120)


    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v261)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v261)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.coil.compose.v230)
    implementation(libs.accompanist.permissions)
    implementation(libs.material.icons.extended)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // TFlite for text classification
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.accompanist.permissions.v0301)

    // Google Maps and Location Services
    implementation(libs.play.services.maps.v1810)
    implementation(libs.play.services.location.v2101)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}