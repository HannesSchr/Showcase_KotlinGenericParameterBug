plugins {
    kotlin("jvm") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.strikt:strikt-core:0.34.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}