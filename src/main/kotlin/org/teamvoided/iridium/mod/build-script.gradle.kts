package org.teamvoided.iridium.mod

import kotlinx.serialization.json.Json
import org.teamvoided.iridium.config.Config.authors
import org.teamvoided.iridium.config.Config.badges
import org.teamvoided.iridium.config.Config.disableKotlin
import org.teamvoided.iridium.config.Config.discordServerInviteId
import org.teamvoided.iridium.config.Config.fabricApiVersion
import org.teamvoided.iridium.config.Config.fabricLangKotlinVersion
import org.teamvoided.iridium.config.Config.fabricLoaderVersion
import org.teamvoided.iridium.config.Config.githubRepo
import org.teamvoided.iridium.config.Config.license
import org.teamvoided.iridium.config.Config.majorMinecraftVersion
import org.teamvoided.iridium.config.Config.mappings
import org.teamvoided.iridium.config.Config.minecraftVersion
import org.teamvoided.iridium.config.IridiumLoader.MappingsType
import org.teamvoided.iridium.helper.makeCustom
import java.io.FileReader
import java.io.FileWriter

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

repositories {
    maven("https://maven.fabricmc.net/")
    if (mappings.type == MappingsType.MOJPARCH || mappings.type == MappingsType.PARCHMENT)
        maven("https://maven.parchmentmc.org")
    if (mappings.type == MappingsType.QUILT)
        maven("https://maven.quiltmc.org/repository/release")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    when (mappings.type) {
        MappingsType.MOJANG -> {
            mappings(loom.officialMojangMappings())
        }

        MappingsType.YARN -> {
            mappings("net.fabricmc:yarn:${mappings.version}")
        }

        MappingsType.PARCHMENT -> {
            mappings(loom.layered {
                parchment("org.parchmentmc.data:parchment-${mappings.version}")
            })
        }

        MappingsType.MOJPARCH -> {
            mappings(loom.layered {
                parchment("org.parchmentmc.data:parchment-${mappings.version}")
                officialMojangMappings()
            })
        }

        MappingsType.MOJYARN -> {
            mappings(loom.layered {
                mappings("net.fabricmc:yarn:${mappings.version}")
                officialMojangMappings()
            })
        }

        MappingsType.QUILT -> {
            mappings("org.quiltmc:quilt-mappings:${mappings.version}:intermediary-v2")
        }

        MappingsType.CUSTOM -> {
            mappings(file(mappings.version!!))
        }
    }

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    if (!disableKotlin) modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLangKotlinVersion")
}

val buildScriptExtension: BuildScriptExtension = extensions.create("modSettings", BuildScriptExtension::class.java)

afterEvaluate {
    val modId = buildScriptExtension.modId()
    val modName = buildScriptExtension.modName()
    val modEntrypoints = buildScriptExtension.entrypoints()
    val modMixinFiles = buildScriptExtension.mixinFiles()
    val modDepends = buildScriptExtension.dependencies()
    val isModParent = buildScriptExtension.isModParent()
    val modParent = buildScriptExtension.modParent()
    val customModIcon: String? = buildScriptExtension.customIcon()
    val accessWidener = buildScriptExtension.accessWidener()
    val supportsTransition = buildScriptExtension.supportsTransition()

    version = buildScriptExtension.modVersion
    group = buildScriptExtension.group
    base.archivesName.set(modId)

    tasks {
        val copyLicenses = register("copyLicenses") {
            val resourceDir = buildDir.resolve("resources/main")

            val files = rootDir.listFiles()!!
            val regex = Regex("(?i)LICENSE\\.?.*")

            val licenseFiles = files.filter { regex.matches(it.name) }
            val newFiles = licenseFiles.map {
                resourceDir.resolve(
                    it.toString().removePrefix("$rootDir${File.separator}")
                ).absoluteFile
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
                "$modName was built with the Iridium gradle plugin developed by TeamVoided over at https://teamvoided.org"
            val creditsFile = buildDir.resolve("resources/main/credits.iridium")

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

        val modJsonTask = register("modJson") {
            val modConfig = ModConfiguration(
                1,
                modId,
                buildScriptExtension.modVersion,
                modName,
                buildScriptExtension.modDescription,
                authors,
                modEntrypoints.mapValuesTo(LinkedHashMap()) {
                    it.value.map { target -> ModConfiguration.Entrypoint("kotlin", target) }
                },
                modMixinFiles,
                accessWidener,
                linkedMapOf(
                    "fabric-api" to "*",
                    "fabric-language-kotlin" to ">=1.8.0+kotlin.1.7.0",
                    "minecraft" to majorMinecraftVersion
                ).apply { putAll(modDepends) },
                ModConfiguration.Contact(
                    "https://github.com/$githubRepo",
                    "https://github.com/$githubRepo/issues",
                    "https://github.com/$githubRepo",
                    "https://discord.gg/$discordServerInviteId"
                ),
                license,
                if (isModParent) "assets/$modId/icon.png" else customModIcon,
                makeCustom(isModParent, modParent, badges, supportsTransition),
            )
            if (disableKotlin) {
                modConfig.entrypoints =
                    LinkedHashMap(modConfig.entrypoints.mapValues { op -> op.value.map { it.adapter = ""; it } })
                modConfig.depends = LinkedHashMap(modConfig.depends.filter { it.key != "fabric-language-kotlin" })
            }

            val modDotJson = buildDir.resolve("resources/main/fabric.mod.json")

            inputs.property("modConfig", modConfig.toString())
            outputs.file(modDotJson)

            doFirst {
                ModConfigurationMutations.applyMutations(project.name, modConfig)

                val prettyJson = Json { prettyPrint = true }

                if (!modDotJson.exists()) {
                    modDotJson.parentFile.mkdirs()
                    modDotJson.createNewFile()
                }

                modDotJson.writeText(prettyJson.encodeToString(modConfig))
            }
        }

        processResources {
            dependsOn(copyLicenses)
            dependsOn(creditsTask)
            dependsOn(modJsonTask)
        }
    }
}
