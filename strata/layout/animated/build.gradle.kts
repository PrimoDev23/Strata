import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Not published yet: add mavenPublish and abiValidation once the public API settles
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    explicitApi()

    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "dev.leonp.strata.animated"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.libraryMinSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.foundation)
        }
    }
}
