import dev.detekt.gradle.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    // provides the root check task that detekt hooks into
    base
    alias(libs.plugins.detekt)
}

detekt {
    source.setFrom(
        fileTree(projectDir) {
            include("**/src/**/*.kt", "**/*.kts")
            exclude("**/build/**", "**/.gradle/**")
        },
    )
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
}

// Points git to the versioned hooks, so they are active after the first Gradle sync
providers.exec {
    commandLine("git", "config", "core.hooksPath", ".githooks")
    isIgnoreExitValue = true
}.result.get()

dependencies {
    detektPlugins(libs.detekt.ktlintWrapper)
    detektPlugins(libs.detekt.composeRules)
}

tasks.register<Detekt>("detektFormat") {
    description = "Runs detekt and auto corrects formatting issues."
    setSource(detekt.source)
    config.setFrom(detekt.config)
    buildUponDefaultConfig = detekt.buildUponDefaultConfig
    parallel = detekt.parallel
    autoCorrect = true
}
