plugins {
    id("java")

    kotlin("jvm") version "2.2.20"

    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "com.marcpg.pillarperil"
version = "0.2.1"
description = "Open-Source & customizable \"Pillars of Fortune\"-like game — spawn on bedrock pillars, get random items, and dominate!"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://marcpg.com/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases/")
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

    implementation("dev.faststats.metrics:bukkit:0.15.0")

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
        relocate("dev.faststats", "$group.libs.faststats")
    }
}
