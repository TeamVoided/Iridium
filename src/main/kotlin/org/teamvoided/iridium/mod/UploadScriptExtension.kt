package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import java.util.*

open class UploadScriptExtension {
    private var modrinthId: String? = null
    private var customModrinthTokenProperty: String? = null

    private val modrinthDependencies = LinkedList<ModDependency>()
    val REQUIRED = DependencyType.REQUIRED
    val OPTIONAL = DependencyType.OPTIONAL
    val INCOMPATIBLE = DependencyType.INCOMPATIBLE
    val EMBEDDED = DependencyType.EMBEDDED

    fun modrinthId(): String? = modrinthId

    fun modrinthId(modrinthId: String) { this.modrinthId = modrinthId }
    fun customModrinthTokenProperty() = customModrinthTokenProperty

    fun customModrinthTokenProperty(customModrinthTokenProperty: String) { this.customModrinthTokenProperty = customModrinthTokenProperty }
    fun modrinthDependencies() = modrinthDependencies

    fun modrinthDependency(modrinthId: String, type: DependencyType) {
        modrinthDependencies += ModDependency(modrinthId, type)
    }
}