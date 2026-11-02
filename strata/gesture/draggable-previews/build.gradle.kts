import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Not published: keeps the preview and theme dependencies out of the draggable artifact
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "dev.leonp.strata.draggable.previews"
        compileSdk {
            // Compose and material3 require API 37.2, a minor SDK release that a plain integer cannot express
            version = release(libs.versions.android.compileSdk.get().toInt()) {
                minorApiLevel = libs.versions.android.compileSdkMinor.get().toInt()
            }
        }
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(project(":theme"))
            implementation(project(":strata:gesture:draggable"))
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
