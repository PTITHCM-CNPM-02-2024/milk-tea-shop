rootProject.name = "mts_backend"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")



buildCache { 
    local { 
        directory = File(rootDir, "build-cache")
    }    
}
includeBuild(".")
includeBuild("infrastructure")
includeBuild("platform")
includeBuild("bootstrap")
includeBuild("domain")
includeBuild("application")
