import org.flywaydb.gradle.task.*

plugins{
    id("mts-backend.java-application-convention")
    id("org.springframework.boot") version "3.4.2"
    id("org.flywaydb.flyway") version "11.3.2"
}

repositories{
    gradlePluginPortal()
    mavenCentral()
}

dependencies{
    implementation(("com.mts.backend:infrastructure"))
    implementation(platform("com.mts.backend:platform"))
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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


group = "com.mts.backend"
version = "1.0-SNAPSHOT"

tasks.withType<Test>{
    useJUnitPlatform()
}

