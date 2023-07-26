package org.teamvoided.iridium

import org.teamvoided.iridium.config.IridiumLoader.Mappings
import org.teamvoided.iridium.config.IridiumLoader.MappingsType
import org.teamvoided.iridium.config.IridiumLoader.config

open class IridiumExtension {
    val MOJANG = MappingsType.MOJANG
    val YARN = MappingsType.YARN
    val PARCHMENT = MappingsType.PARCHMENT
    val QUILT = MappingsType.QUILT
    val MOJPARCH = MappingsType.MOJPARCH
    val MOJYARN = MappingsType.MOJYARN
    internal var dirty = false

    fun projectTitle() = config.projectTitle
    fun projectTitle(projectTitle: String) { config.projectTitle = projectTitle; dirty = true }

    fun modId() = config.modId
    fun modId(modId: String) { config.modId = modId; dirty = true }

    fun githubRepo() = config.githubRepo
    fun githubRepo(githubRepo: String) { config.githubRepo = githubRepo; dirty = true }

    fun discordServerInviteId() = config.discordServerInviteId
    fun discordServerInviteId(discordServerInviteId: String) { config.discordServerInviteId = discordServerInviteId; dirty = true }

    fun authors() = config.authors
    fun authors(vararg authors: String) { config.authors = authors.toList(); dirty = true }

    fun majorMinecraftVersion() = config.majorMinecraftVersion
    fun majorMinecraftVersion(majorMinecraftVersion: String) { config.majorMinecraftVersion = majorMinecraftVersion; dirty = true }

    fun minecraftVersion() = config.minecraftVersion
    fun minecraftVersion(minecraftVersion: String) { config.minecraftVersion = minecraftVersion; dirty = true }

    fun mappings() = config.mappings
    fun mappings(type: MappingsType, version: String) { config.mappings = Mappings(type, version); dirty = true }

    fun fabricLoaderVersion() = config.fabricLoaderVersion
    fun fabricLoaderVersion(fabricLoaderVersion: String) { config.fabricLoaderVersion = fabricLoaderVersion; dirty = true }

    fun fabricApiVersion() = config.fabricApiVersion
    fun fabricApiVersion(fabricApiVersion: String) { config.fabricApiVersion = fabricApiVersion; dirty = true }

    fun fabricLangKotlinVersion() = config.fabricLangKotlinVersion
    fun fabricLangKotlinVersion(fabricLangKotlinVersion: String) { config.fabricLangKotlinVersion = fabricLangKotlinVersion; dirty = true }

    fun license() = config.license
    fun license(license: String) { config.license = license; dirty = true }

    fun modules() = config.modules
    fun modules(vararg modules: String) { config.modules = modules.toList(); dirty = true }
}