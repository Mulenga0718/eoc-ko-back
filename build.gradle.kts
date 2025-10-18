plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
    java
}

group = "com.ibs"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
    }
}

// 버전 카탈로그
val mapstructVersion = "1.5.5.Final"
val springdocVersion = "2.5.0"
val testcontainersVersion = "1.19.8"
val jjwtVersion = "0.11.5"
val querydslVersion = "5.0.0"
val resilience4jVersion = "2.2.0"

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database (MySQL)
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Security (JWT)
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // API Docs
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // Monitoring
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot3:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-micrometer:$resilience4jVersion")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta") // querydsl-jpa로 변경
    annotationProcessor("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Utils
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("com.github.ben-manes.caffeine:caffeine")


    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("commons-io:commons-io:2.11.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mysql:$testcontainersVersion")

}

sourceSets {
    main {
        java.srcDir("$buildDir/generated/sources/annotationProcessor/java/main")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-parameters"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
