package org.teamvoided.iridium.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.annotations.TomlComments
import io.github.xn32.json5k.Json5
import io.github.xn32.json5k.SerialComment
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
        @SerialComment("Json5 is been DEPRECATED, please switch to TOML")
        var projectTitle: String,
        var modId: String,
        var modVersion: String,
        var group: String,
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
            "1.0.0",
            "com.example.mod",
            "yourGithubNameHere/repositoryNameHere",
            "discordServerInviteIdHere",
            listOf("your-name-here", "some1-elses-name-here"),
            ">=26.1",
            "26.1",
            Mappings(MappingsType.NONE, null),
            "0.18.5",
            "0.144.3+26.1",
            "1.13.10+kotlin.2.3.20",
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
            "CUSTOM: Custom mapping jar. Uses mappings version as file location",
            "NONE: Use for unmapped versions of the game (26.1 and up)."
        )
        val type: MappingsType,
        val version: String?,
    )

    @Serializable
    enum class MappingsType {
        MOJANG, YARN, PARCHMENT, QUILT, MOJPARCH, MOJYARN, CUSTOM, NONE;

        fun remaps() = this != MappingsType.NONE
    }
}
