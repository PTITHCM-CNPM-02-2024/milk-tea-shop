rootProject.name = "bootstrap"

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

includeBuild("../platform")
