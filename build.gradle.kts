plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
    id("maven-publish")
}

group = "org.teamvoided.iridium"
version = "1.2.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    val kotlinVersion = "1.8.21"

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))

    implementation(pluginDep("fabric-loom", "1.2-SNAPSHOT"))
    implementation(pluginDep("com.modrinth.minotaur", "2.7.5"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.github.xn32:json5k:0.3.0")
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

        create("iridium-mod-jar-in-jar") {
            id = "iridium.mod.jar-in-jar"
            implementationClass = "org.teamvoided.iridium.mod.JarInJarPlugin"
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
            name = "BrokenFuse"
            url = uri("https://maven.brokenfuse.me/releases")
            credentials {
                username = System.getenv("BFUSE_USER")
                password = System.getenv("BFUSE_KEY")
            }
        }
    }
}