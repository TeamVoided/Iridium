package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.teamvoided.iridium.config.Config.minecraftVersion
import org.teamvoided.iridium.config.Config.projectState
import org.teamvoided.iridium.config.Config.projectTitle


plugins {
    kotlin("jvm")

    id("fabric-loom")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

val uploadScriptExtension = extensions.create("uploadConfig", UploadScriptExtension::class.java)

afterEvaluate {
    val modrinthId = uploadScriptExtension.modrinthId
    val curseId = uploadScriptExtension.curseId?.trim()

    val modrinthDeps = uploadScriptExtension.modrinthDependencies()
    val curseDeps = uploadScriptExtension.curseDependencies()

    val versions: List<String> = if (uploadScriptExtension.versionOverrides.isNullOrEmpty()) listOf(minecraftVersion)
    else uploadScriptExtension.versionOverrides!!
    val name = if (uploadScriptExtension.versionName == null) "$projectTitle ${rootProject.version}"
    else uploadScriptExtension.versionName

    if (modrinthId == null) {
        println("Property \"modrinthId\" not found. Skipping Modrinth...")
    } else {
        modrinth {
            token.set(
                uploadScriptExtension.customModrinthTokenProperty()
                    ?: System.getProperty("MODRINTH_TOKEN")
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

            versionName = name

            if (uploadScriptExtension.changeLog != null) changelog = uploadScriptExtension.changeLog

            debugMode = uploadScriptExtension.debugMode
        }

        tasks.register("publishToModrinth") {
            group = "publishing"
            dependsOn("modrinth")
        }
    }

    if (curseId == null) {
        println("Property \"curseId\" not found. Skipping CurseForge...")
    } else {
        tasks.register<net.darkhax.curseforgegradle.TaskPublishCurseForge>("publishToCurseForge") {
            group = "publishing"
            apiToken = (
                    uploadScriptExtension.customCurseTokenProperty()
                        ?: System.getProperty("CURSE_FORGE_TOKEN")
                        ?: "ERROR"
                    )
            debugMode = uploadScriptExtension.debugMode

            // The main file to upload
            upload(curseId, tasks.remapJar.get()) {
                displayName = name
                releaseType = projectState

                versions.forEach { gameVersions.add(it) }
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
                changelogType = "markdown"
                changelog = if (uploadScriptExtension.changeLog != null) uploadScriptExtension.changeLog else " "
            }
        }
    }

    if (modrinthId != null && curseId != null) {
        tasks.register("publishToAllModPlatforms") {
            group = "publishing"
            dependsOn("modrinth", "publishToCurseForge")
            doLast {
                println("\nMods published!")
            }
        }
    }
}
