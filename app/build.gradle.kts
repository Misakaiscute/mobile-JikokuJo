import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.dagger.hilt.android)
}

val realApi: String =
    gradleLocalProperties(rootDir, providers).getProperty("api_protocol", "http") + "://" +
    gradleLocalProperties(rootDir, providers).getProperty("api_host", "localhost") + ':' +
    gradleLocalProperties(rootDir, providers).getProperty("api_port", "8000") + '/' +
    gradleLocalProperties(rootDir, providers).getProperty("api_suffix", "")

private val hostOnEmu: String = if (gradleLocalProperties(rootDir, providers).getProperty("api_host", "localhost") == "localhost") {
        "10.0.2.2"
    } else {
        gradleLocalProperties(rootDir, providers).getProperty("api_host", "localhost")
    }
val emuApi: String =
    gradleLocalProperties(rootDir, providers).getProperty("api_protocol", "http") + "://" +
    hostOnEmu + ':' +
    gradleLocalProperties(rootDir, providers).getProperty("api_port", "8000") + '/' +
    gradleLocalProperties(rootDir, providers).getProperty("api_suffix", "")


project.extensions.configure<ApplicationExtension>("android") {
    namespace = "com.jikokujo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.jikokuj"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "EMULATED_DEVICE_API", "\"$emuApi\"")
        buildConfigField("String", "PHYSICAL_DEVICE_API", "\"$realApi\"")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.dagger.hilt.android)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    ksp(libs.dagger.hilt.android.compiler)

    implementation(libs.retrofit)

    implementation(libs.gson.serialization)
    implementation(libs.gson.serialization.converter)

    implementation(libs.mapsforge.core)
    implementation(libs.mapsforge.map)
    implementation(libs.mapsforge.map.reader)
    implementation(libs.mapsforge.android)
    implementation(libs.mapsforge.themes)

    implementation(libs.navigation.ui)
    implementation(libs.navigation.runtime)

    implementation(libs.datastore)

    testImplementation(libs.coroutine.testing)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}