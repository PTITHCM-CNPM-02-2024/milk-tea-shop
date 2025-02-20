plugins{
    java
}

repositories{
    mavenCentral()
}

java{
    toolchain{
        languageVersion = JavaLanguageVersion.of(23)
    }
}
group = "com.mts.backend"
version = "1.0-SNAPSHOT"


tasks.withType<Test>{
    useJUnitPlatform()
}