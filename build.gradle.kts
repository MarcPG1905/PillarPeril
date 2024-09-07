plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "com.marcpg.pillarperil"
version = "0.1.0"
description = "Open-Source and Customizable version of CubeCraft's \"Pillars of Fortune\" game mode, but even better!"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://marcpg.com/repo/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    implementation("com.marcpg:libpg:0.1.1")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.21.1")
    }
    shadowJar {
        archiveClassifier.set("")
        manifest {
            // This is just so paper won't remap the plugin.
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
    processResources {
        filter {
            it.replace("\${version}", version.toString())
        }
    }
}
