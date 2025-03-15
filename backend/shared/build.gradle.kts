plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    implementation("org.springframework.boot:spring-boot-starter")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

