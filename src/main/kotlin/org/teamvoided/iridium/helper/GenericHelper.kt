package org.teamvoided.iridium.helper

import org.teamvoided.iridium.mod.ModConfiguration

fun resolveVersion(version: String): String {
    return if (version.trim() == "*") "*" else ">=$version"
}

fun resolveMCVersion(version: String): String {
    return if (version.trim() == "*") "*" else "~$version"
}

fun makeCustom(isModParent: Boolean, modParent: String?, badges: List<String>): ModConfiguration.Custom? {
    if (isModParent && badges.isEmpty()) return null
    return modMenu(modParent, badges)
}

fun modMenu(parent: String?, badges: List<String>): ModConfiguration.Custom =
    ModConfiguration.Custom(ModConfiguration.Custom.ModMenu(parent, badges))
