package org.teamvoided.iridium.config

import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import io.github.xn32.json5k.Json5
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object IridiumLoader {
    private val defaultConfig = Config(
        "Example Title",
        "mod_id_here",
        "yourGithubNameHere/repositoryNameHere", "discordServerInviteIdHere",
        listOf("your-name-here", "some1-elses-name-here"),
        "1.20",
        "1.20.1",
        Mappings(MappingsType.YARN, "1.20.1+build.9"),
        "0.14.21",
        "0.84.0+1.20.1",
        "1.10.0+kotlin.1.9.0",
        "MIT",
        listOf("some-module-here", "some-other-module-here"),
        listOf(ModrinthDep("dependencyId1", DepType.REQUIRED), ModrinthDep("dependencyId2", DepType.REQUIRED))
    )

    var config: Config = defaultConfig

    fun loadFrom(file: File) {
        val json5 = Json5 {
            prettyPrint = true
            indentationWidth = 2
        }

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            val json5String = json5.encodeToString(defaultConfig)
            FileWriter(file).use {
                it.write(json5String)
            }
            return
        }

        val json5String = FileReader(file).use { it.readText() }
        config = json5.decodeFromString(json5String)
    }

    @Serializable
    data class Config(
        val projectTitle: String,
        val modId: String,
        val githubRepo: String,
        val discordServerInviteId: String,
        val authors: List<String>,
        val majorMinecraftVersion: String,
        val minecraftVersion: String,
        val mappings: Mappings,
        val fabricLoaderVersion: String,
        val fabricApiVersion: String,
        val fabricLangKotlinVersion: String,
        val license: String,
        val modules: List<String>,
        val modrinthDependencies: List<ModrinthDep>
    )

    @Serializable
    data class Mappings(
        @SerialComment(
            "You have 6 mapping types\nMOJANG: The official Mojang mappings\nYARN: Yarn mappings\nPARCHMENT: Parchment mappings\nQUILT: Quilt mappings\nMOJPARCH: Parchment layered on top of the official Mojang mappings\nMOJYARN: Yarn layered on top of the official Mojang mappings"
        ) val type: MappingsType,
        val version: String?
    )

    @Serializable
    enum class MappingsType { MOJANG, YARN, PARCHMENT, QUILT, MOJPARCH, MOJYARN }
    @Serializable
    data class ModrinthDep(val id: String, @SerialComment("Any of REQUIRED | OPTIONAL | INCOMPATIBLE | EMBEDDED") val type: DepType) {
        fun toModrinthApiType(): ModDependency = ModDependency(id, type.toModrinthApiType())
    }

    @Serializable
    enum class DepType {
        REQUIRED, OPTIONAL, INCOMPATIBLE, EMBEDDED;

        fun toModrinthApiType(): DependencyType {
            return when (this) {
                REQUIRED -> DependencyType.REQUIRED
                OPTIONAL -> DependencyType.REQUIRED
                INCOMPATIBLE -> DependencyType.INCOMPATIBLE
                EMBEDDED -> DependencyType.EMBEDDED
            }
        }
    }
}

