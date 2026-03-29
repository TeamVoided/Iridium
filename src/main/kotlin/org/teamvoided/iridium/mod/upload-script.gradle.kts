package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import net.darkhax.curseforgegradle.TaskPublishCurseForge


plugins {
    kotlin("jvm")

    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

val uploadScript = extensions.create("uploadScript", UploadScriptExtension::class.java)

afterEvaluate {
    val modrinthId = uploadScript.modrinthId
    val curseId = uploadScript.curseId?.trim()

    if ((modrinthId != null || curseId != null) && uploadScript.jarTask == null) {
        println("[ERROR] No jar was found canceling task")
        return@afterEvaluate
    }

    val modrinthDeps = uploadScript.modrinthDependencies()
    val curseDeps = uploadScript.curseDependencies()

    val versions: List<String> = uploadScript.version
    if (versions.isEmpty()) {
        println("[ERROR] Version list should not be empty")
        return@afterEvaluate
    }

    val name = uploadScript.versionName
    if (name == null) {
        println("[ERROR] Version name should be null")
        return@afterEvaluate
    }



    if (modrinthId != null) {
        modrinth {
            token.set(
                uploadScript.customModrinthTokenProperty ?: System.getProperty("MODRINTH_TOKEN") ?: "ERROR"
            )

            projectId.set(modrinthId)
            versionNumber.set(rootProject.version.toString())

            gameVersions.set(versions)
            loaders.set(listOf("fabric"))

            uploadFile.set(uploadScript.jarTask)

            if (uploadScript.autoAddDependsOn) autoAddDependsOn = true
            if (modrinthDeps.isNotEmpty()) dependencies.set(modrinthDeps)

            versionName = name

            if (uploadScript.changelog != null) changelog = uploadScript.changelog

            debugMode = uploadScript.debugMode
        }

        tasks.register("publishToModrinth") {
            group = "publishing"
            dependsOn("modrinth")
        }
    }

    if (curseId != null) {
        tasks.register<TaskPublishCurseForge>("publishToCurseForge") {
            group = "publishing"
            apiToken =
                (uploadScript.customCurseTokenProperty ?: System.getProperty("CURSE_FORGE_TOKEN") ?: "ERROR")
            debugMode = uploadScript.debugMode

            upload(curseId, uploadScript.jarTask) {
                displayName = name

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
                changelog = if (uploadScript.changelog != null) uploadScript.changelog else " "
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
