plugins {
    id("mts-backend.java-library-convention")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    api("com.mts.backend:domain")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter")
}
