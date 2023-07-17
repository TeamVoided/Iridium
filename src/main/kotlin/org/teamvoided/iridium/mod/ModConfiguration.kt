package org.teamvoided.iridium.mod

import kotlinx.serialization.Serializable

@Serializable
data class ModConfiguration(
    var schemaVersion: Int,
    var id: String,
    var version: String,
    var name: String,
    var description: String,
    var authors: List<String>,
    var entrypoints: LinkedHashMap<String, List<Entrypoint>> = linkedMapOf(),
    var mixins: List<String> = emptyList(),
    var depends: LinkedHashMap<String, String>,
    var contact: Contact,
    var license: String,
    var icon: String? = null,
    var custom: Custom? = null,
    var jars: MutableList<JarFile> = mutableListOf(),
    var languageAdapters: LinkedHashMap<String, String> = linkedMapOf()
) {
    fun contact(homepage: String, issues: String, sources: String, discord: String) =
        Contact(homepage, issues, sources, discord)
    @Serializable
    data class Contact(
        val homepage: String,
        val issues: String,
        val sources: String,
        val discord: String,
    )

    fun entrypoint(adapter: String, value: String) =
        Entrypoint(adapter, value)
    @Serializable
    data class Entrypoint(
        val adapter: String,
        val value: String,
    )

    fun custom(modmenu: Custom.ModMenu?) =
        Custom(modmenu)
    fun modMenu(parent: String) =
        Custom.ModMenu(parent)
    @Serializable
    data class Custom(
        val modmenu: ModMenu? = null,
    ) {
        @Serializable
        data class ModMenu(
            val parent: String,
        )
    }

    fun jarFile(file: String) =
        JarFile(file)
    @Serializable
    data class JarFile(val file: String)
}