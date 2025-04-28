plugins {
    `java-platform`
    id("org.springframework.boot") version "3.4.2"
}


repositories {
    google()

    mavenCentral()
    gradlePluginPortal()
}

javaPlatform {
    allowDependencies()
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

extra["springModulithVersion"] = "1.3.1"

dependencies {
    constraints {
        api("org.springframework.boot:spring-boot-starter:3.4.4")
        api("org.springframework.boot:spring-boot-starter-data-jdbc:3.4.4")
        api("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")
        api("org.springframework.boot:spring-boot-starter-jdbc:3.4.4")
        api("org.springframework.boot:spring-boot-starter-security:3.4.4")
        api("org.springframework.boot:spring-boot-starter-web:3.4.4")
        api("org.springframework.boot:spring-boot-starter-test:3.4.4")
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
        api("org.flywaydb:flyway-core:11.3.2")
        api("org.flywaydb:flyway-mysql:11.3.2")
        api("org.projectlombok:lombok:1.18.36")
        api("org.springframework.boot:spring-boot-configuration-processor:3.4.4")
        runtime("com.mysql:mysql-connector-j:9.2.0")
        api("org.springframework.modulith:spring-modulith-starter-core:1.3.2")
        api("org.springframework.modulith:spring-modulith-starter-jdbc:1.3.2")
        api("org.springframework.modulith:spring-modulith-starter-jpa:1.3.2")
        api("org.springframework.boot:spring-boot-starter-logging:3.4.4")
        api("org.springframework.boot:spring-boot-starter-actuator:3.4.4")
        api("org.springframework.boot:spring-boot-starter-validation:3.4.4")
        api("io.jsonwebtoken:jjwt-api:0.12.6")
        api("org.apache.httpcomponents.client5:httpclient5:5.4.3")
        api("com.google.firebase:firebase-bom:33.8.0")
        api("io.swagger.core.v3:swagger-annotations:2.2.30")
    }
}

springBoot{
    buildInfo()
}
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}