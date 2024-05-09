plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "com.marcpg"
version = "0.0.2"
description = "Open-Source and Customizable version of CubeCraft's \"Pillars of Fortune\" game mode, but even better!"

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
    implementation("com.marcpg:libpg:0.1.1")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier = null
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
