plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.plugin.serialization) version embeddedKotlinVersion
    id("maven-publish")
}

val dotenvFile = File("$projectDir/.env")
if (dotenvFile.exists()) {
    dotenvFile.forEachLine { line ->
        val (key, value) = line.split("=", limit = 2)
        if (key.isNotBlank() && value.isNotBlank()) System.setProperty(key, value)
    }
    println("Loaded .env vars!")
} else println("No .env file found! No variables to load")

group = property("group")!!
version = property("version")!!

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    val kotlinVersion = libs.versions.kotlin.get()

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))

    implementation(libs.fabric.loom)
    implementation(libs.modrinth.minotaur)
    implementation(libs.curseforgegradle)

    implementation(libs.kotlinx.serialization)
    implementation(libs.json5k)
    implementation(libs.ktoml)
    implementation(libs.kaml)

}

gradlePlugin {
    plugins {
        create("iridium") {
            id = "org.teamvoided.iridium"
            implementationClass = "org.teamvoided.iridium.IridiumPlugin"
        }

        create("iridium-mod-build-script") {
            id = "iridium.mod.build-script"
            implementationClass = "org.teamvoided.iridium.mod.BuildScriptPlugin"
        }

        create("iridium-mod-upload-script") {
            id = "iridium.mod.upload-script"
            implementationClass = "org.teamvoided.iridium.mod.UploadScriptPlugin"
        }

        create("iridium-project-publish-script") {
            id = "iridium.project.publish-script"
            implementationClass = "org.teamvoided.iridium.project.PublishScriptPlugin"
        }

        create("iridium-kotlin-project-script") {
            id = "iridium.project.kotlin-script"
            implementationClass = "org.teamvoided.iridium.project.KotlinProjectScriptPlugin"
        }
    }
}

// configure the maven publication
publishing {
    repositories {
        maven {
            name = "TeamVoided"
            url = uri("https://maven.teamvoided.org/releases")

            credentials {
                username = System.getProperty("TeamVoidedUsername")
                    ?: throw NullPointerException("Variable TeamVoidedUsername not found!")
                password = System.getProperty("TeamVoidedPassword")
                    ?: throw NullPointerException("Variable TeamVoidedPassword not found!")
            }
        }
    }
}