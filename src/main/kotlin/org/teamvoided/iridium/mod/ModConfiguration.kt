package org.teamvoided.iridium.mod

import kotlinx.serialization.Serializable

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
    val jars: MutableList<JarFile> = mutableListOf(),
    val languageAdapters: LinkedHashMap<String, String> = linkedMapOf()
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