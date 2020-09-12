import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71"
    kotlin("kapt") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = ""
version = "1.0"

var ktor_version = "1.3.2"

repositories {

    jcenter()

    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype-oss"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "enginehub-maven"
        url = uri("http://maven.enginehub.org/repo/")
    }
    maven {
        name = "myndocs-oauth2"
        url = uri("https://dl.bintray.com/adhesivee/oauth2-server")
    }
    maven {
        name = "aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        name = "codemc-repo"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        name = "minecraft-repo"
        url = uri("https://libraries.minecraft.net")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/exposed")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "co.aikar", name = "acf-paper", version = "0.5.0-SNAPSHOT")

    /*implementation(group = "io.ktor", name = "ktor-server-netty", version = "1.3.2")
    implementation(group = "io.ktor", name = "ktor-auth", version = "1.3.2")
    implementation(group = "io.ktor", name = "ktor-client-apache", version = "1.3.2")
    implementation(group = "io.ktor", name = "ktor-html-builder", version = "1.3.2")*/

    implementation(group = "org.jetbrains", name = "kotlin-css", version = "1.0.0-pre.104-kotlin-1.3.72")
    implementation(group = "org.jetbrains.exposed", name = "exposed-core", version = "0.27.1")
    implementation(group = "org.jetbrains.exposed", name = "exposed-jdbc", version = "0.27.1")

    implementation(group = "com.uchuhimo", name = "konf", version = "0.22.1")

    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.11.0")

    compileOnly(group = "com.comphenix.protocol", name = "ProtocolLib", version = "4.5.0")
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "1.16.2-R0.1-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldedit", name = "worldedit-bukkit", version = "7.2.0-SNAPSHOT")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "boater.acf")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
