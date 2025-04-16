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
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-admin:9.4.3")
    implementation("io.swagger.core.v3:swagger-annotations")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

