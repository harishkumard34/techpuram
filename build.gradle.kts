plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail") // Spring Mail Starter
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Jakarta Mail API and Activation API
    implementation("jakarta.mail:jakarta.mail-api:2.0.0") // Jakarta Mail API 2.0.0
    implementation("jakarta.activation:jakarta.activation-api:2.1.3") // Jakarta Activation API 2.1.3

    // Re-add com.sun.mail for compatibility (only at runtime)
    runtimeOnly("com.sun.mail:jakarta.mail:1.6.7") // Required at runtime for compatibility

    // Lombok for compile-time annotations
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // PostgreSQL JDBC driver for database connection
    runtimeOnly("org.postgresql:postgresql")

    // Spring Boot DevTools for development
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
