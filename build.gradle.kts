plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web API
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Validation (@Valid, @NotBlank 등)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // JPA/Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // DB Migration
    implementation("org.flywaydb:flyway-core")
    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql")
    // Actuator (health/metrics)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.flywaydb:flyway-core:10.22.0")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.22.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
