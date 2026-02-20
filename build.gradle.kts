plugins {
    id("java")

    kotlin("jvm") version "2.2.20"

    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "com.marcpg.pillarperil"
version = "0.2.0"
description = "Open-Source and Customizable version of CubeCraft's \"Pillars of Fortune\" game mode, but even better!"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases/")
    maven("https://marcpg.com/repo/")
}

private fun DependencyHandler.implementationOrFile(notation: String, fallback: String) {
    val dep = dependencies.create(notation)
    val config = configurations.detachedConfiguration(dep)

    try {
        check(config.resolve().isNotEmpty())
        implementation(notation)
    } catch (_: Exception) {
        implementation(files(fallback))
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    implementationOrFile("com.marcpg:ktlibpg-platform-paper:2.0.2", "libs/ktlibpg-platform-paper-2.0.2.jar")

    compileOnly(kotlin("stdlib"))
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
