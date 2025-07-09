plugins {
    id("java")
    id("application")
    id("io.freefair.lombok") version "8.6"
}

group = "dev.haiseong"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("dev.haiseong.Main")
}
