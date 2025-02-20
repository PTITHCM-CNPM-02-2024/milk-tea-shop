
plugins{
    `java-library`
    id("mts-backend.java-convention")
}

repositories{
    gradlePluginPortal()
    mavenCentral()
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"

dependencies{
    api(platform(("com.ptithcm.mts-backend:platform")))
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.4.2")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
