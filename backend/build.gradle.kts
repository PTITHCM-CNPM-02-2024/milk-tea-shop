import org.flywaydb.gradle.task.*

plugins{
    id("mts-backend.java-application-convention")
    id("org.springframework.boot") version "3.4.2"
    id("org.flywaydb.flyway") version "11.3.2"
}

repositories{
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies{
    implementation("com.mts.backend:api")
    implementation("com.mts.backend:domain")
    implementation("com.mts.backend:shared")
    implementation("com.mts.backend:infrastructure")
    implementation("com.mts.backend:application")
    implementation(platform("com.mts.backend:platform"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.4.2")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-admin:9.4.3")
}

tasks.register("flywayCleanDev", FlywayCleanTask::class){
    group = true
    description = "Clean database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/dev")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-dev.conf")
}

tasks.register("flywayMigrateDev", FlywayMigrateTask::class){
    group = true
    description = "Migrate database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/dev")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-dev.conf")
}

tasks.register("flywayBaseLineDev", FlywayBaselineTask::class){
    group = true
    description = "Baseline database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/dev")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-dev.conf")
}

tasks.register("flywayMigrateProd", FlywayMigrateTask::class){
    group = true
    description = "Migrate database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/prod")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-prod.conf")
}

tasks.register("flywayBaseLineProd", FlywayBaselineTask::class){
    group = true
    description = "Baseline database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/prod")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-prod.conf")
}

tasks.register("flywayInfoDev", FlywayInfoTask::class){
    group = true
    description = "Info database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/dev")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-dev.conf")
}

tasks.register("flywayRepairDev", FlywayRepairTask::class){
    group = true
    description = "Repair database"
    this.locations = arrayOf("filesystem:src/main/resources/db/migration/dev")
    this.configFiles = arrayOf("src/main/java/config/flyway/flyway-dev.conf")
}


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.mysql:mysql-connector-j:9.2.0")
        classpath("org.flywaydb:flyway-core:11.3.2")
        classpath("org.flywaydb:flyway-mysql:11.3.2")
    }
}


group = "com.mts"
version = "1.0-SNAPSHOT"

tasks.withType<Test>{
    useJUnitPlatform()
}

tasks.withType<JavaCompile>{
    options.compilerArgs.add("-parameters")
}

