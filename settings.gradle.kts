rootProject.name = "Strata"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":demo")
include(":theme")
include(":strata:gesture:draggable")
include(":strata:gesture:draggable-previews")
include(":strata:gesture:draggable-baselineprofile")
include(":strata:layout:animated")
include(":strata:layout:animated-baselineprofile")