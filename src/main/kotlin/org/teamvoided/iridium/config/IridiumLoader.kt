package org.teamvoided.iridium.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.annotations.TomlComments
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlComment
import io.github.xn32.json5k.Json5
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okio.use
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object IridiumLoader {
    val defaultConfig = Config(
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
        listOf("some-module-here", "some-other-module-here")
    )

    var config: Config = defaultConfig

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
            val stringData = format.encodeToString(defaultConfig)
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
        var modules: List<String>
    )

    @Serializable
    data class Mappings(
        @YamlComment(
            "You have 6 mapping types", "MOJANG: The official Mojang mappings", "YARN: Yarn mappings", "PARCHMENT: Parchment mappings", "QUILT: Quilt mappings", "MOJPARCH: Parchment layered on top of the official Mojang mappings", "MOJYARN: Yarn layered on top of the official Mojang mappings"
        ) @TomlComments(
            "You have 6 mapping types", "MOJANG: The official Mojang mappings", "YARN: Yarn mappings", "PARCHMENT: Parchment mappings", "QUILT: Quilt mappings", "MOJPARCH: Parchment layered on top of the official Mojang mappings", "MOJYARN: Yarn layered on top of the official Mojang mappings"
        ) @SerialComment(
            "You have 6 mapping types\nMOJANG: The official Mojang mappings\nYARN: Yarn mappings\nPARCHMENT: Parchment mappings\nQUILT: Quilt mappings\nMOJPARCH: Parchment layered on top of the official Mojang mappings\nMOJYARN: Yarn layered on top of the official Mojang mappings"
        ) val type: MappingsType,
        val version: String?
    )

    @Serializable
    enum class MappingsType { MOJANG, YARN, PARCHMENT, QUILT, MOJPARCH, MOJYARN }
}

