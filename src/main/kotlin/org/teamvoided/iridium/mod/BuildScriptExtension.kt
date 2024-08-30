package org.teamvoided.iridium.mod

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.teamvoided.iridium.config.Config
import org.teamvoided.iridium.helper.resolveMCVersion
import org.teamvoided.iridium.helper.resolveVersion
import java.io.File

open class BuildScriptExtension(val project: Project) {
    private var modId = Config.modId
    private var modName = Config.projectTitle
    private var modEntrypoints = linkedMapOf<String, List<String>>()
    private var modMixinFiles = mutableListOf<String>()
    private var accessWidener: String? = null
    private var modDepends = linkedMapOf(
        "fabricloader" to "*",
        "fabric-api" to "*",
        "fabric-language-kotlin" to resolveVersion(Config.fabricLangKotlinVersion),
        "minecraft" to resolveMCVersion(Config.majorMinecraftVersion),
        "java" to ">=17"
    )
    private var isModParent = false
    private val modParent get() = if (!isModParent) Config.modId else null
    private var customModIcon: String? = null
    var supportsTransition = false

    fun modId() = modId
    fun modId(modId: String) {
        this.modId = modId
    }

    fun modName() = modName
    fun modName(modName: String) {
        this.modName = modName
    }

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
    fun mixinFile(mixinFile: String) {
        modMixinFiles += mixinFile
    }

    fun accessWidener() = accessWidener
    fun accessWidener(accessWidener: String) {
        this.accessWidener = accessWidener
        (project.extensions["loom"] as LoomGradleExtensionAPI).accessWidenerPath.set(File("src/main/resources/$accessWidener"))
    }

    fun dependencies() = modDepends
    fun dependency(id: String, versionDeclaration: String) {
        modDepends[id] = versionDeclaration
    }

    fun isModParent() = isModParent
    fun isModParent(isModParent: Boolean) {
        this.isModParent = isModParent
    }

    fun modParent() = modParent

    fun customIcon() = customModIcon
    fun customIcon(iconPath: String) {
        customModIcon = iconPath
    }

    fun supportsTransition() = supportsTransition
    fun enableTransition() {
        supportsTransition = true
    }

    fun mutation(mutation: ModConfiguration.() -> Unit) =
        ModConfigurationMutations.addMutation(project.name) { mutation(it) }
}