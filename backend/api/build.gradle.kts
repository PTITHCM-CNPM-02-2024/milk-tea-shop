plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    api("com.mts.backend:infrastructure")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

