rootProject.name = "domain"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

includeBuild("../shared")


