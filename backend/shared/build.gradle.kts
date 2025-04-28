plugins{
    id("mts-backend.java-library-convention")
}


repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}