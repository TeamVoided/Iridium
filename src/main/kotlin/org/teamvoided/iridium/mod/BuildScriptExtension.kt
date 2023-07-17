package org.teamvoided.iridium.mod

import org.gradle.api.Project
import org.teamvoided.iridium.config.Config

open class BuildScriptExtension(val project: Project) {
    private var modId = Config.modId
    private var modName = Config.projectTitle
    private var modEntrypoints = linkedMapOf<String, List<String>>()
    private var modMixinFiles = mutableListOf<String>()
    private var modDepends = linkedMapOf(
        Pair("fabricloader", ">=${Config.fabricLoaderVersion}"),
        Pair("fabric-api", ">=${Config.fabricApiVersion}"),
        Pair("fabric-language-kotlin", ">=${Config.fabricLangKotlinVersion}"),
        Pair("minecraft", "~${Config.majorMinecraftVersion}"),
        Pair("java", ">=17")
    )
    private var isModParent = false
    private val modParent get() = if (!isModParent) Config.modId else null
    private var customModIcon: String? = null

    fun modId() = modId
    fun modId(modId: String) { this.modId = modId }

    fun modName() = modName
    fun modName(modName: String) { this.modName = modName }

    fun entrypoints() = modEntrypoints
    fun entrypoint(name: String, value: String) {
        val list = modEntrypoints[name]
        if (list != null) {
            modEntrypoints[name] = (list + value)
            return
        }

        modEntrypoints[name] = listOf(value)
    }

    fun mixinFiles() = modMixinFiles
    fun mixinFile(mixinFile: String) { modMixinFiles += mixinFile }

    fun dependencies() = modDepends
    fun dependency(id: String, versionDeclaration: String) { modDepends[id] = versionDeclaration }

    fun isModParent() = isModParent
    fun isModParent(isModParent: Boolean) { this.isModParent = isModParent }

    fun modParent() = modParent

    fun customIcon() = customModIcon
    fun customIcon(iconPath: String) { customModIcon = iconPath }

    fun mutation(mutation: ModConfiguration.() -> Unit) =
        ModConfigurationMutations.addMutation(project.name) { mutation(it) }
}