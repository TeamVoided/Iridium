package org.teamvoided.iridium

import org.teamvoided.iridium.config.IridiumLoader.Mappings
import org.teamvoided.iridium.config.IridiumLoader.MappingsType
import org.teamvoided.iridium.config.IridiumLoader.defaultConfig

open class IridiumExtension {
    private var projectTitle: String = defaultConfig.projectTitle
    private var modId: String = defaultConfig.modId
    private var githubRepo: String = defaultConfig.githubRepo
    private var discordServerInviteId: String = defaultConfig.discordServerInviteId
    private var authors: List<String> = defaultConfig.authors
    private var majorMinecraftVersion: String = defaultConfig.majorMinecraftVersion
    private var minecraftVersion: String = defaultConfig.minecraftVersion
    private var mappings: Mappings = defaultConfig.mappings
    private var fabricLoaderVersion: String = defaultConfig.fabricLoaderVersion
    private var fabricApiVersion: String = defaultConfig.fabricApiVersion
    private var fabricLangKotlinVersion: String = defaultConfig.fabricLangKotlinVersion
    private var license: String = defaultConfig.license
    private var modules: List<String> = defaultConfig.modules
    internal var dirty = false

    val MOJANG = MappingsType.MOJANG
    val YARN = MappingsType.YARN
    val PARCHMENT = MappingsType.PARCHMENT
    val QUILT = MappingsType.QUILT
    val MOJPARCH = MappingsType.MOJPARCH
    val MOJYARN = MappingsType.MOJYARN

    fun projectTitle() = projectTitle
    fun projectTitle(projectTitle: String) { this.projectTitle = projectTitle; dirty = true }

    fun modId() = modId
    fun modId(modId: String) { this.modId = modId; dirty = true }

    fun githubRepo() = githubRepo
    fun githubRepo(githubRepo: String) { this.githubRepo = githubRepo; dirty = true }

    fun discordServerInviteId() = discordServerInviteId
    fun discordServerInviteId(discordServerInviteId: String) { this.discordServerInviteId = discordServerInviteId; dirty = true }

    fun authors() = authors
    fun authors(vararg authors: String) { this.authors = authors.toList(); dirty = true }

    fun majorMinecraftVersion() = majorMinecraftVersion
    fun majorMinecraftVersion(majorMinecraftVersion: String) { this.majorMinecraftVersion = majorMinecraftVersion; dirty = true }

    fun minecraftVersion() = minecraftVersion
    fun minecraftVersion(minecraftVersion: String) { this.minecraftVersion = minecraftVersion; dirty = true }

    fun mappings() = mappings
    fun mappings(type: MappingsType, version: String) { this.mappings = Mappings(type, version); dirty = true }

    fun fabricLoaderVersion() = fabricLoaderVersion
    fun fabricLoaderVersion(fabricLoaderVersion: String) { this.fabricLoaderVersion = fabricLoaderVersion; dirty = true }

    fun fabricApiVersion() = fabricApiVersion
    fun fabricApiVersion(fabricApiVersion: String) { this.fabricApiVersion = fabricApiVersion; dirty = true }

    fun fabricLangKotlinVersion() = fabricLangKotlinVersion
    fun fabricLangKotlinVersion(fabricLangKotlinVersion: String) { this.fabricLangKotlinVersion = fabricLangKotlinVersion; dirty = true }

    fun license() = license
    fun license(license: String) { this.license = license; dirty = true }

    fun modules() = modules
    fun modules(vararg modules: String) { this.modules = modules.toList(); dirty = true }
}