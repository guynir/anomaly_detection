plugins {
    application
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

val flinkVersion by extra { "1.19.0" }

group = "com.perimeter81"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = "com.ks.detector.DetectorApplication"
    applicationDefaultJvmArgs = listOf(
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Required for utilities.
    implementation("org.springframework:spring-core:6.1.6")

    // JUnit and testing
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.assertj:assertj-core:3.25.3")

    // Apache Flink
    implementation("org.apache.flink:flink-java:${flinkVersion}")
    compileOnly("org.apache.flink:flink-core:${flinkVersion}")
    compileOnly("org.apache.flink:flink-streaming-java:${flinkVersion}")
    implementation("org.apache.flink:flink-clients:${flinkVersion}")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
