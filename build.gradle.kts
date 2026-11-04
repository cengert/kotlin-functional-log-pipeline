plugins {
    kotlin("jvm") version "2.3.21"
}

group = "de.thws.fiw.kotlin.portfolio3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}