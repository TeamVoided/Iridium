package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import java.util.*

open class UploadScriptExtension {
    var modrinthId: String? = null
    var curseId: String? = null
    private var customModrinthTokenProperty: String? = null
    private var customCurseTokenProperty: String? = null
    var autoAddDependsOn: Boolean = false
    var debugMode: Boolean = false
    var versionName: String? = null
    var changeLog: String? = null
    var versionOverrides: List<String>? = null

    private val modrinthDependencies = LinkedList<ModDependency>()
    private val curseDependencies = LinkedList<Pair<String, DependencyType>>()


    val REQUIRED = DependencyType.REQUIRED
    val OPTIONAL = DependencyType.OPTIONAL
    val INCOMPATIBLE = DependencyType.INCOMPATIBLE
    val EMBEDDED = DependencyType.EMBEDDED

    fun customModrinthTokenProperty() = customModrinthTokenProperty

    fun customModrinthTokenProperty(customModrinthTokenProperty: String) {
        this.customModrinthTokenProperty = customModrinthTokenProperty
    }

    fun customCurseTokenProperty() = customCurseTokenProperty

    fun customCurseTokenProperty(customCurseTokenProperty: String) {
        this.customCurseTokenProperty = customCurseTokenProperty
    }

    fun modrinthDependencies() = modrinthDependencies
    fun curseDependencies() = curseDependencies

    fun modrinthDependency(modrinthId: String, type: DependencyType) {
        modrinthDependencies += ModDependency(modrinthId, type)
    }

    fun curseDependency(curseId: String, type: DependencyType) {
        curseDependencies += curseId to type
    }
}