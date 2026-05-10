plugins {
    java

    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)

    alias(libs.plugins.jpnilla.runPaper)
    alias(libs.plugins.paperweight.userdev)

    alias(libs.plugins.versionCatalogueUpdate)
}

group = "com.marcpg.pillarperil"
version = "0.2.2"
description = "Open-Source & customizable \"Pillars of Fortune\"-like game — spawn on bedrock pillars, get random items, and dominate!"

versionCatalogUpdate {
    pin { // Don't auto-update these.
        versions.add("kotlin")
        versions.add("paper")
    }
    keep {
        versions.add("paper")
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenLocal()
    mavenCentral()

//    maven("https://marcpg.com/repo/")
    maven("https://repo.faststats.dev/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
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

    implementationOrFile(libs.ktlibpg.paper, "libs/ktlibpg-platform-paper-${libs.versions.ktlibpg.get()}.jar")
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
        minecraftVersion("26.1.1")
    }
    shadowJar {
        archiveClassifier.set("")
        relocate("dev.faststats", "$group.libs.faststats")
    }
}
