rootProject.name = "domain"

include(":common", ":product")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

