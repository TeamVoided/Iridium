plugins {
    `kotlin-dsl`
//    kotlin("plugin.serialization") version embeddedKotlinVersion
    alias(libs.plugins.kotlin.plugin.serialization) version embeddedKotlinVersion
    id("maven-publish")
}

group = "org.teamvoided.iridium"
version = "3.1.10"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

//val dotenvFile = File("${project.projectDir}/.env")
//if (dotenvFile.exists()) {
//    dotenvFile.forEachLine { line ->
//        val (key, value) = line.split("=", limit = 2)
//        if (key.isNotBlank() && value.isNotBlank()) {
//            println("[$key,$value]")
//            System.setProperty(key, value)
//        }
//    }
//} else {
//    println("No .env file found! No variables to load")
//}
//val myEnvVar = System.getProperty("KEY")
//println(myEnvVar)

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    val kotlinVersion = libs.versions.kotlin.get()

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    /*
    implementation(pluginDep("fabric-loom", "1.6-SNAPSHOT"))
    implementation(pluginDep("com.modrinth.minotaur", "2.7.5"))
     */
    implementation(libs.fabric.loom)
    implementation(libs.modrinth.minotaur)
    implementation(libs.curseforgegradle)

    /*
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.github.xn32:json5k:0.3.0")
    implementation("com.charleskorn.kaml:kaml:0.54.0")
     */
    implementation(libs.kotlinx.serialization)
    implementation(libs.json5k)
//    implementation(libs.ktoml) // This breaks and im too lazy to fix it rn
    implementation("com.akuleshov7:ktoml-core:0.5.0")
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
            credentials(PasswordCredentials::class)
        }
    }
}
