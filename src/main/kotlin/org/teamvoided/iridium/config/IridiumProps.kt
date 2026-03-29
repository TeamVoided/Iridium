package org.teamvoided.iridium.config

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import okio.use
import org.teamvoided.iridium.utils.TOML
import org.teamvoided.iridium.utils.processNode
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object IridiumProps {
    var modId: String = "template"
    var modVersion: String = "1.0.0"
    var modGroup: String = "com.exmaple"
    var githubRepo: String = "Exmaple/Repo"
    var license: String = "MIT"
    var authors: List<String> = mutableListOf()


    val properties = mutableMapOf<String, String>()

    fun loadFrom(file: File, forceLoad: Boolean): Boolean {
        if (attemptLoad(File("${file}.toml"))) return true
        if (forceLoad) {
            forceLoad(Toml, File("${file}.toml"))
            return true
        }

        return false
    }

    private fun attemptLoad(file: File): Boolean {
        if (!file.exists()) return false
        try {
            val stringData = FileReader(file).use { it.readText() }
            TOML.tomlParser.parseString(stringData).children.forEach { node ->
                processNode(properties, node)
            }
        } catch (e: Exception) {
            println("[ERROR] Failed to parse props file!")
            e.printStackTrace()
        }

        properties["mod_id"]?.let { modId = it } ?: noPropsWarning("mod_id")
        properties["mod_version"]?.let { modVersion = it } ?: noPropsWarning("mod_version")
        properties["mod_group"]?.let { modGroup = it } ?: noPropsWarning("mod_group")
        properties["github_repo"]?.let { githubRepo = it } ?: noPropsWarning("github_repo")
        properties["license"]?.let { license = it } ?: noPropsWarning("license")
        properties["authors"]?.let { authors = it.split(",").map(String::trim) } ?: noPropsWarning("authors")

        return properties.isNotEmpty()
    }

    fun noPropsWarning(name: String) {
        println("[WARNING] No \"$name\" found in properties toml, using default!")
    }


    private fun forceLoad(format: StringFormat, file: File) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            val stringData = format.encodeToString(DefaultValues())
            FileWriter(file).use { it.write(stringData) }
            return
        }
    }

    @Suppress("PropertyName")
    @Serializable
    data class DefaultValues(
        var mod_id: String = "template",
        var mod_version: String = "1.0.0",
        var mod_group: String = "com.exmaple",
        var github_repo: String = "Exmaple/Repo",
        var license: String = "MIT",
        var authors: List<String> = mutableListOf("TheIridiumCore"),
    )
}
