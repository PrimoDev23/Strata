import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Generates the baseline profile of the draggable library by driving its components in the demo app.
// Run ./gradlew :strata:gesture:draggable:generateBaselineProfile with an emulator or device running.
plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.baselineProfile)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

android {
    namespace = "dev.leonp.strata.draggable.baselineprofile"
    compileSdk {
        // Compose and material3 require API 37.2, a minor SDK release that a plain integer cannot express
        version = release(libs.versions.android.compileSdk.get().toInt()) {
            minorApiLevel = libs.versions.android.compileSdkMinor.get().toInt()
        }
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    targetProjectPath = ":androidApp"
}

baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.test.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
