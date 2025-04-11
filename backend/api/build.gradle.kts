plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies{
    api("com.mts.backend:infrastructure")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("com.google.firebase:firebase-firestore:25.1.3")
    api("com.google.firebase:firebase-admin:9.4.3")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

