rootProject.name = "backend"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

buildCache { 
    local { 
        directory = File(rootDir, "build-cache")
    }    
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
}
includeBuild(".")
includeBuild("shared")
includeBuild("domain")
includeBuild("application")
includeBuild("infrastructure")
includeBuild("api")
includeBuild("platform")
