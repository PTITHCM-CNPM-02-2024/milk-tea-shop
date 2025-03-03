plugins {
    `kotlin-dsl`
}

repositories{
    gradlePluginPortal()
    mavenCentral()
}


dependencies{
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.4.2")
}

group = "com.mts.backend.build-logic"
version = "1.0-SNAPSHOT"
