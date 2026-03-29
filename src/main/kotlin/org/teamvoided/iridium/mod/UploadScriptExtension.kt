package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import org.gradle.jvm.tasks.Jar
import java.util.*

open class UploadScriptExtension {
    var modrinthId: String? = null
    var curseId: String? = null
    var customModrinthTokenProperty: String? = null
    var customCurseTokenProperty: String? = null
    var jarTask: Jar? = null
    var autoAddDependsOn: Boolean = false
    var debugMode: Boolean = false
    var versionName: String? = null
    var changelog: String? = null
    var version: MutableList<String> = mutableListOf()

    private val modrinthDependencies = LinkedList<ModDependency>()
    private val curseDependencies = LinkedList<Pair<String, DependencyType>>()

    val REQUIRED = DependencyType.REQUIRED
    val OPTIONAL = DependencyType.OPTIONAL
    val INCOMPATIBLE = DependencyType.INCOMPATIBLE
    val EMBEDDED = DependencyType.EMBEDDED

    fun modrinthDependencies() = modrinthDependencies
    fun curseDependencies() = curseDependencies

    fun modrinthDependency(modrinthId: String, type: DependencyType) {
        modrinthDependencies += ModDependency(modrinthId, type)
    }

    fun curseDependency(curseId: String, type: DependencyType) {
        curseDependencies += curseId to type
    }

    fun dependency(
        modrinthId: String? = null, curseId: String? = null, type: DependencyType = DependencyType.REQUIRED,
    ) {
        if (modrinthId != null) modrinthDependencies.add(ModDependency(modrinthId, type))
        if (curseId != null) curseDependencies.add(curseId to type)
    }
}