
plugins {
    id("mts-backend.java-library-convention")
    id("org.springframework.boot")
}

group = "com.mts.backend.infrastructure"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-security")
}

tasks.getByName("bootJar") {
    enabled = false
}




