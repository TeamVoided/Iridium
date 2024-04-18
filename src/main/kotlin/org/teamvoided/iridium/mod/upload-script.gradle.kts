package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.teamvoided.iridium.config.Config.minecraftVersion
import org.teamvoided.iridium.config.Config.projectState


plugins {
    kotlin("jvm")

    id("fabric-loom")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

val uploadScriptExtension = extensions.create("modrinthConfig", UploadScriptExtension::class.java)

afterEvaluate {
    val modrinthId = uploadScriptExtension.modrinthId
    val curseId = uploadScriptExtension.curseId

    val modrinthDeps = uploadScriptExtension.modrinthDependencies()
    val curseDeps = uploadScriptExtension.curseDependencies()

    val versions: List<String> = if (uploadScriptExtension.versionOverrides.isNullOrEmpty()) listOf(minecraftVersion)
    else uploadScriptExtension.versionOverrides!!

    if (modrinthId == null) {
        println("Property \"modrinthId\" not found. Skipping Modrinth...")
    } else {
        modrinth {
            token.set(
                uploadScriptExtension.customModrinthTokenProperty()
                    ?: System.getenv("MODRINTH_TOKEN")
                    ?: "ERROR"
            )

            projectId.set(modrinthId)
            versionNumber.set(rootProject.version.toString())
            versionType.set(projectState)

            gameVersions.set(versions)
            loaders.set(listOf("fabric"))

            uploadFile.set(tasks.remapJar.get())

            if (uploadScriptExtension.autoAddDependsOn) autoAddDependsOn = true
            if (modrinthDeps.isNotEmpty()) dependencies.set(modrinthDeps)

            versionName = if (uploadScriptExtension.versionName == null) "${rootProject.name} ${rootProject.version}"
            else uploadScriptExtension.versionName

            if (uploadScriptExtension.changeLog != null) changelog = uploadScriptExtension.changeLog

            debugMode = uploadScriptExtension.debugMode
        }
    }

    if (curseId == null) {
        println("Property \"curseId\" not found. Skipping CurseForge...")
    } else {
        tasks.register<net.darkhax.curseforgegradle.TaskPublishCurseForge>("publishCurseForge") {
            apiToken = uploadScriptExtension.customCurseTokenProperty() ?: System.getProperty("CURSE_FORGE_TOKEN")
            debugMode = uploadScriptExtension.debugMode

            // The main file to upload
            upload(curseId, tasks.remapJar.get()) {
                displayName =
                    if (uploadScriptExtension.versionName == null) "${rootProject.name} ${rootProject.version}"
                    else uploadScriptExtension.versionName
                releaseType = projectState

                gameVersions = versions.toSet()
                addModLoader("Fabric")
                if (curseDeps.isNotEmpty()) {
                    curseDeps.forEach { (id, type) ->
                        when (type) {
                            DependencyType.REQUIRED -> addRequirement(id)
                            DependencyType.OPTIONAL -> addOptional(id)
                            DependencyType.INCOMPATIBLE -> addIncompatibility(id)
                            DependencyType.EMBEDDED -> addEmbedded(id)
                        }
                    }
                }

                if (uploadScriptExtension.changeLog != null) {
                    changelog = uploadScriptExtension.changeLog
                    changelogType = "markdown"
                }
            }
        }
    }

}