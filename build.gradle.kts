plugins {
    java

    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)

    alias(libs.plugins.jpnilla.runPaper)
    alias(libs.plugins.paperweight.userdev)
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
    maven("https://repo.faststats.dev/releases/")
}

private fun DependencyHandler.implementationOrFile(notation: Provider<MinimalExternalModuleDependency>, fallback: String) {
    val dep = notation.get()
    val config = configurations.detachedConfiguration(dep)

    try {
        check(config.resolve().isNotEmpty())
        implementation(notation)
    } catch (_: Exception) {
        implementation(files(fallback))
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    paperweight.paperDevBundle(libs.versions.paper.get())

    implementationOrFile(libs.ktlibpg.paper, "libs/ktlibpg-platform-paper-2.0.2.jar")
    implementation(libs.faststats)
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
