package org.teamvoided.iridium.mod

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.use
import org.gradle.api.Project
import org.teamvoided.iridium.config.IridiumProps
import org.teamvoided.iridium.utils.JSON
import org.teamvoided.iridium.utils.TOML
import org.teamvoided.iridium.utils.processNode
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.net.URI

open class BuildScriptExtension(val project: Project) {
    var modId = IridiumProps.modId
    var modVersion = IridiumProps.modVersion
    var modGroup = IridiumProps.modGroup
    var githubRepo = IridiumProps.githubRepo
    var license = IridiumProps.license
    var authors = IridiumProps.authors
    var props = IridiumProps.properties

    fun modName() = getOrError("mod_name")
    fun modDescription() = getOrError("mod_description")
    fun minMinecraft() = getOrError("min_minecraft")
    fun maxMinecraft() = getOrError("max_minecraft")

    fun getOrError(name: String) = props[name] ?: error("Missing prop \"$name\"")

    fun appendLibsVersionProps(map: MutableMap<String, String>, file: File) {
        if (!file.exists()) {
            println("[WARNING] libs.versions file not found!")
            return
        }
        try {
            val stringData = FileReader(file).use { it.readText() }
            for (node in TOML.tomlParser.parseString(stringData).children) {
                if (node.name == "versions") {
                    processNode(map, node, rawName = true)
                }
            }
        } catch (e: Exception) {
            println("[ERROR] Failed to parse libs.versions file!")
            e.printStackTrace()
        }
    }

    var uuidFile = project.rootDir.resolve(".gradle/uuid")
    fun fetchUUID(name: String): String? {
        if (name == "vDev" || name == "Dev") return null
        var id = ""
        try {
            if (uuidFile.exists()) {
                val player = JSON.decodeFromString<PlayerUUID>(uuidFile.readText())
                if (player.name == name) {
                    id = player.id
                }
            }

            if (id == "") {
                id = getFromUrl(name) ?: return null
                if (!uuidFile.parentFile.exists()) {
                    uuidFile.parentFile.mkdirs()
                }
                if (uuidFile.exists()) {
                    uuidFile.delete()
                }
                uuidFile.createNewFile()
                uuidFile.writeText(JSON.encodeToString(PlayerUUID(name, id)))
            }
        } catch (e: Exception) {
            println("[Error] There was a problem during uuid Fetching")
            println(e)
        }

        return if (id == "") null else id
    }

    private fun getFromUrl(name: String): String? {
        val url = URI("https://api.mojang.com/users/profiles/minecraft/$name").toURL()
        try {
            val con = url.openConnection()
            val br = BufferedReader(InputStreamReader(con.getInputStream()))
            val json = JSON.parseToJsonElement(br.readText()).jsonObject
            return json["id"]?.jsonPrimitive?.content
        } catch (_: Exception) {
            println("[Warn] Failed to tech username [$name]")
        }
        return null
    }

    @Serializable
    data class PlayerUUID(var name: String, var id: String)
}