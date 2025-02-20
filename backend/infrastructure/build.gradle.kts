plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    implementation((projects.persistence))
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

