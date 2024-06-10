package org.teamvoided.iridium.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.annotations.TomlComments
import com.charleskorn.kaml.Yaml
import io.github.xn32.json5k.Json5
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okio.use
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object IridiumLoader {

    var config: Config = Config()

    @OptIn(ExperimentalSerializationApi::class)
    fun loadFrom(file: File, forceLoad: Boolean): Boolean {
        if (attemptLoad(Toml, File("${file}.toml"))) return true
        if (attemptLoad(Json5 { prettyPrint = true; indentationWidth = 2 }, File("${file}.json5"))) return true
        if (attemptLoad(Yaml.default, File("${file}.yml"))) return true
        if (attemptLoad(Json { prettyPrint = true; prettyPrintIndent = "\t" }, File("${file}.json"))) return true
        if (forceLoad) {
            forceLoad(Toml, File("${file}.toml"))
            return true
        }

        return false
    }

    private fun attemptLoad(format: StringFormat, file: File): Boolean {
        if (!file.exists()) return false
        val stringData = FileReader(file).use { it.readText() }
        config = format.decodeFromString(stringData)

        return true
    }

    private fun forceLoad(format: StringFormat, file: File) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            val stringData = format.encodeToString(Config())
            FileWriter(file).use { it.write(stringData) }
            return
        }

        val stringData = FileReader(file).use { it.readText() }
        config = format.decodeFromString(stringData)
    }

    @Serializable
    data class Config(
        var projectTitle: String,
        var modId: String,
        var githubRepo: String,
        var discordServerInviteId: String,
        var authors: List<String>,
        var majorMinecraftVersion: String,
        var minecraftVersion: String,
        var mappings: Mappings,
        var fabricLoaderVersion: String,
        var fabricApiVersion: String,
        var fabricLangKotlinVersion: String,
        var license: String,
        var modules: List<String>,
        var modDescription: String,
        @TomlComments("Modmenu badges, values can be: [ library, deprecated ]")
        var badges: List<String>,
        var disableKotlin: Boolean,
    ) {
        constructor() : this(
            "Example Title",
            "mod_id_here",
            "yourGithubNameHere/repositoryNameHere",
            "discordServerInviteIdHere",
            listOf("your-name-here", "some1-elses-name-here"),
            "1.20",
            "1.20.1",
            Mappings(MappingsType.YARN, "1.20.1+build.9"),
            "0.15.11",
            "0.84.0+1.20.1",
            "1.11.0+kotlin.2.0.0",
            "MIT",
            listOf(),
            "Very Good Description",
            listOf(),
            false
        )
    }

    @Serializable
    data class Mappings(
        @TomlComments(
            "You have 7 mapping types",
            "MOJANG: The official Mojang mappings",
            "YARN: Yarn mappings",
            "PARCHMENT: Parchment mappings",
            "QUILT: Quilt mappings",
            "MOJPARCH: Parchment layered on top of the official Mojang mappings",
            "MOJYARN: Yarn layered on top of the official Mojang mappings",
            "CUSTOM: Custom mapping jar. Uses mappings version as file location"
        )
        val type: MappingsType, val version: String?
    )

    @Serializable
    enum class MappingsType { MOJANG, YARN, PARCHMENT, QUILT, MOJPARCH, MOJYARN, CUSTOM }
}
