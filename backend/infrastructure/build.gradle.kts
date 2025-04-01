plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    api("com.mts.backend:application")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("jakarta.persistence:jakarta.persistence-api")
    implementation("io.jsonwebtoken:jjwt-api")
// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

