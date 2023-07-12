package org.teamvoided.iridium.mod

import org.teamvoided.iridium.config.Config.minecraftVersion
import org.teamvoided.iridium.config.Config.modrinthDependencies
import org.teamvoided.iridium.config.Config.projectState

plugins {
    kotlin("jvm")

    id("fabric-loom")
    id("com.modrinth.minotaur")
}

val uploadScriptExtension = extensions.create("modrinthConfig", UploadScriptExtension::class.java)

afterEvaluate {
    val modrinthId = uploadScriptExtension.modrinthId() ?: throw IllegalStateException("property \"modrinthId\" cannot be null")
    val customSysProperty = uploadScriptExtension.customModrinthTokenProperty()

    modrinth {
        token.set(findProperty(if (customSysProperty != null) customSysProperty else "modrinth.token").toString())

        projectId.set(modrinthId)
        versionNumber.set(rootProject.version.toString())
        versionType.set(projectState)
        gameVersions.set(listOf(minecraftVersion))
        loaders.set(listOf("fabric"))

        uploadFile.set(tasks.remapJar.get())

        if (modrinthDependencies.isNotEmpty()) {
            dependencies.set(modrinthDependencies.map { it.toModrinthApiType() })
        }
    }
}