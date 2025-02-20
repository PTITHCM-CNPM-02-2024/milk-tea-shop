rootProject.name = "infrastructure"

include(":persistence")

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
