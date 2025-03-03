plugins {
    id("mts-backend.java-library-convention")
}

group = "com.mts.backend.domain"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}