package org.teamvoided.iridium.helper

import org.teamvoided.iridium.mod.ModConfiguration

fun resolveVersion(version: String): String {
    return if (version.trim() == "*") "*" else ">=$version"
}

fun resolveMCVersion(version: String): String {
    return if (version.trim() == "*") "*" else "~$version"
}

fun makeCustom(
    isModParent: Boolean, modParent: String?,
    badges: List<String>, supportsTransition: Boolean
): ModConfiguration.Custom? {
    if (isModParent && !supportsTransition && badges.isEmpty()) return null
    return ModConfiguration.Custom(if (badges.isEmpty()) null else modMenu(modParent, badges), supportsTransition)
}

fun modMenu(parent: String?, badges: List<String>): ModConfiguration.Custom.ModMenu =
    ModConfiguration.Custom.ModMenu(parent, badges)
