package org.teamvoided.iridium.mod

import org.teamvoided.iridium.config.Config.minecraftVersion
import org.teamvoided.iridium.config.Config.projectState

plugins {
    kotlin("jvm")

    id("fabric-loom")
    id("com.modrinth.minotaur")
}

val uploadScriptExtension = extensions.create("modrinthConfig", UploadScriptExtension::class.java)

afterEvaluate {
    val modrinthId = uploadScriptExtension.modrinthId() ?: throw IllegalStateException("property \"modrinthId\" cannot be null")
    val modrinthDeps = uploadScriptExtension.modrinthDependencies()

    modrinth {
        token.set(findProperty(uploadScriptExtension.customModrinthTokenProperty() ?: "modrinth.token").toString())

        projectId.set(modrinthId)
        versionNumber.set(rootProject.version.toString())
        versionType.set(projectState)
        gameVersions.set(listOf(minecraftVersion))
        loaders.set(listOf("fabric"))

        uploadFile.set(tasks.remapJar.get())

        if (modrinthDeps.isNotEmpty()) {
            dependencies.set(modrinthDeps)
        }
    }
}