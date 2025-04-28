plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    google()
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
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.3")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-admin:9.4.3")
    
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
