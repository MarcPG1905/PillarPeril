plugins {
    id("java")

    kotlin("jvm") version "2.2.0"

    id("com.gradleup.shadow") version "8.3.8"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "com.marcpg.pillarperil"
version = "0.1.0"
description = "Open-Source and Customizable version of CubeCraft's \"Pillars of Fortune\" game mode, but even better!"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases/")
    maven("https://marcpg.com/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    implementation(files("libs/ktlibpg-platform-brigadier-2.0.0.jar"))
    implementation(files("libs/ktlibpg-platform-adventure-2.0.0.jar"))
    implementation(files("libs/ktlibpg-platform-paper-2.0.0.jar"))

    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
}

tasks {
    reobfJar {
        dependsOn(jar)
    }
    build {
        dependsOn(shadowJar, reobfJar)
    }
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.21.8")
    }
    shadowJar {
        archiveClassifier.set("")
    }
}
