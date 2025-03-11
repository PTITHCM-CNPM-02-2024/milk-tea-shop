rootProject.name = "infrastructure"

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

includeBuild("../application")
