package org.teamvoided.iridium.config

import org.gradle.api.Project

object Config {
    val Project.isSnapshot get() = version.toString().endsWith("-SNAPSHOT")
    val Project.projectState get() = if (isSnapshot) "beta" else "release"

    val projectTitle
        get() = IridiumLoader.config.projectTitle

    val modId
        get() = IridiumLoader.config.modId

    val githubRepo
        get() = IridiumLoader.config.githubRepo

    val discordServerInviteId
        get() = IridiumLoader.config.discordServerInviteId

    val authors
        get() = IridiumLoader.config.authors

    val majorMinecraftVersion
        get() = IridiumLoader.config.majorMinecraftVersion

    val minecraftVersion
        get() = IridiumLoader.config.minecraftVersion

    val mappings
        get() = IridiumLoader.config.mappings

    val fabricLoaderVersion
        get() = IridiumLoader.config.fabricLoaderVersion

    val fabricApiVersion
        get() = IridiumLoader.config.fabricApiVersion

    val fabricLangKotlinVersion
        get() = IridiumLoader.config.fabricLangKotlinVersion

    val license
        get() = IridiumLoader.config.license

    val modules
        get() = IridiumLoader.config.modules

    val modDescription
        get() = IridiumLoader.config.modDescription

    val badges
        get() = IridiumLoader.config.badges

    val disableKotlin
        get() = IridiumLoader.config.disableKotlin
}