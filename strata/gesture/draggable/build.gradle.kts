import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

// The iOS klibs take their unique name from the project group, which must stay stable once published
group = providers.gradleProperty("GROUP").get()

// Group, version and the shared POM metadata come from the root gradle.properties
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(artifactId = "gesture-draggable")

    pom {
        name = "Strata Gesture Draggable"
        description =
            "Collapsible headers and expandable content whose animations follow the drag, for Compose Multiplatform."
    }
}

kotlin {
    explicitApi()

    // Fails the check task when the public API differs from the committed api dump
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "dev.leonp.strata.draggable"
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
