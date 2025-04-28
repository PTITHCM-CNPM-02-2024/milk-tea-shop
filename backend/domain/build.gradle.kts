plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    api("com.mts.backend:shared")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("jakarta.persistence:jakarta.persistence-api")
    api("org.springframework.boot:spring-boot-starter-validation")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}