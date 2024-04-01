plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "com.marcpg"
version = "0.0.1"
description = "Funny game about long sticks and random events."

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://marcpg.com/repo/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("com.marcpg:libpg:0.1.0")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    processResources {
        filter {
            it.replace("\${version}", version.toString())
        }
    }
    runServer {
        minecraftVersion("1.20.4")
    }
}
