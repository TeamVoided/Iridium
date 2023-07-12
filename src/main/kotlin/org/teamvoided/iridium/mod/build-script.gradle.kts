package org.teamvoided.iridium.mod

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.teamvoided.iridium.config.Config.authors
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
    }

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLangKotlinVersion")
}

val buildScriptExtension: BuildScriptExtension = extensions.create("modSettings", BuildScriptExtension::class.java)
extensions.create("dependencyHelper", DependencyHelperExtension::class.java)

@Serializable
data class ModConfiguration(
    val schemaVersion: Int,
    val id: String,
    val version: String,
    val name: String,
    val description: String,
    val authors: List<String>,
    val entrypoints: LinkedHashMap<String, List<Entrypoint>> = linkedMapOf(),
    val mixins: List<String> = emptyList(),
    val depends: LinkedHashMap<String, String>,
    val contact: Contact,
    val license: String,
    val icon: String? = null,
    val custom: Custom? = null,
) {
    @Serializable
    data class Contact(
        val homepage: String,
        val issues: String,
        val sources: String,
        val discord: String,
    )

    @Serializable
    data class Entrypoint(
        val adapter: String,
        val value: String,
    )

    @Serializable
    data class Custom(
        val modmenu: ModMenu? = null,
    ) {
        @Serializable
        data class ModMenu(
            val parent: String,
        )
    }
}

afterEvaluate {
    val modId = buildScriptExtension.modId()
    val modName = buildScriptExtension.modName()
    val modEntrypoints = buildScriptExtension.entrypoints()
    val modMixinFiles = buildScriptExtension.mixinFiles()
    val modDepends = buildScriptExtension.dependencies()
    val isModParent = buildScriptExtension.isModParent()
    val modParent = buildScriptExtension.modParent()
    val customModIcon: String? = buildScriptExtension.customIcon()

    tasks {
        val creditsTask = register("iridiumCredits") {
            val credits = "$modName was built with the Iridium gradle plugin developed by TeamVoided over at https://teamvoided.org"
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
                project.version.toString(),
                modName,
                project.description.toString(),
                authors,
                modEntrypoints.mapValuesTo(LinkedHashMap()) {
                    it.value.map { target -> ModConfiguration.Entrypoint("kotlin", target) }
                },
                modMixinFiles,
                linkedMapOf(
                    "fabric-api" to "*",
                    "fabric-language-kotlin" to ">=1.8.0+kotlin.1.7.0",
                    "minecraft" to "${majorMinecraftVersion}.x"
                ).apply { putAll(modDepends) },
                ModConfiguration.Contact(
                    "https://github.com/$githubRepo",
                    "https://github.com/$githubRepo/issues",
                    "https://github.com/$githubRepo",
                    "https://discord.gg/$discordServerInviteId"
                ),
                license,
                if (modId.endsWith("-all")) "assets/$modId/icon.png" else customModIcon,
                if (isModParent) null else ModConfiguration.Custom(ModConfiguration.Custom.ModMenu(modParent!!)),
            )

            val modDotJson = buildDir.resolve("resources/main/fabric.mod.json")

            inputs.property("modConfig", modConfig.toString())
            outputs.file(modDotJson)

            doFirst {
                val prettyJson = Json { prettyPrint = true }

                if (!modDotJson.exists()) {
                    modDotJson.parentFile.mkdirs()
                    modDotJson.createNewFile()
                }

                modDotJson.writeText(prettyJson.encodeToString(modConfig))
            }
        }

        processResources {
            dependsOn(creditsTask)
            dependsOn(modJsonTask)
        }
    }
}
