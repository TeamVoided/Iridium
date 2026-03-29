package org.teamvoided.iridium.mod

import java.io.FileReader
import java.io.FileWriter

plugins {
    kotlin("jvm")
}

val buildScriptExtension: BuildScriptExtension = extensions.create("iridium", BuildScriptExtension::class.java)

afterEvaluate {
    val modId = buildScriptExtension.modId

    version = buildScriptExtension.modVersion
    group = buildScriptExtension.modGroup
    base.archivesName.set(modId)

    tasks {
        val copyLicenses = register("copyLicenses") {
            val resourceDir = layout.buildDirectory.asFile.get().resolve("resources/main")

            val files = rootDir.listFiles()!!
            val regex = Regex("(?i)LICENSE\\.?.*")

            val licenseFiles = files.filter { regex.matches(it.name) }
            val newFiles = licenseFiles.map {
                resourceDir.resolve(it.toString().removePrefix("$rootDir${File.separator}")).absoluteFile
            }

            outputs.files(newFiles)

            doFirst {
                newFiles.forEachIndexed { index, file ->
                    file.parentFile.mkdirs()
                    file.createNewFile()
                    val data = FileReader(licenseFiles[index]).use { it.readText() }
                    FileWriter(file).use { it.write(data) }
                }
            }
        }

        val creditsTask = register("iridiumCredits") {
            val credits =
                "\"$modId\" was built with the Iridium gradle plugin developed by TeamVoided over at https://teamvoided.org"
            val creditsFile = layout.buildDirectory.asFile.get().resolve("resources/main/credits.iridium")

            inputs.property("credits", credits)
            outputs.file(creditsFile)

            doFirst {
                if (!creditsFile.exists()) {
                    creditsFile.parentFile.mkdirs()
                    creditsFile.createNewFile()
                }

                creditsFile.writeText(credits)
            }
        }

        processResources {
            dependsOn(copyLicenses)
            dependsOn(creditsTask)
        }
    }
}
