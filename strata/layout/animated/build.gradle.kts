import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.baselineProfileConsumer)
}

// The iOS klibs take their unique name from the project group, which must stay stable once published
group = providers.gradleProperty("GROUP").get()

// Group, version and the shared POM metadata come from the root gradle.properties
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(artifactId = "layout-animated")

    pom {
        name = "Strata Layout Animated"
        description =
            "A column that animates its items to their new position when the list changes, for Compose Multiplatform."
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

// Ships the profile in the AAR, so apps precompile the placement and fade code on install
baselineProfile {
    variants {
        create("androidMain") {
            from(project(":strata:layout:animated-baselineprofile"))
        }
    }

    // The generator records the whole demo app, only the library's own rules belong in its profile
    filter {
        include("dev.leonp.strata.animated.**")
    }
}
