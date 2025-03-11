rootProject.name = "shared"

pluginManagement{
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
