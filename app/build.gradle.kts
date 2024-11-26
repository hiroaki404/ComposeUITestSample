plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.roborazziGradlePlugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltGradlePlugin)
}

android {
    namespace = "com.example.composeuitestsample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.composeuitestsample"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.composeuitestsample.MyTestRunner"
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

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
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
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.roborazzi.core)
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.robolectric)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
//    implementation(libs.androidx.hilt.compiler)
//    implementation(libs.androidx.hilt.navigation)
    testImplementation(libs.dagger.hilt.testing)
    androidTestImplementation(libs.dagger.hilt.testing)
    androidTestImplementation(libs.androidx.test.runner)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)
}
