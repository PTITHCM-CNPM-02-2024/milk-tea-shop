plugins{
    id("mts-backend.java-library-convention")
}

dependencies{
    implementation("org.springframework.boot:spring-boot-starter")
    api("com.mts.backend:shared")
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"