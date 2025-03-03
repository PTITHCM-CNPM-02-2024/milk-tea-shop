plugins{
    id("mts-backend.java-library-convention")
}

dependencies{
    implementation(projects.common)
    implementation(projects.product)
}

group = "com.mts.backend"
version = "1.0-SNAPSHOT"