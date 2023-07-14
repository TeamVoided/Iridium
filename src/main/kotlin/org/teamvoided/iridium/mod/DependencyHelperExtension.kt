package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.project

open class DependencyHelperExtension(val project: Project) {
    fun modProject(path: String, dependencyType: DependencyType = DependencyType.REQUIRED): ProjectDependency {
        if (project.plugins.hasPlugin("iridium.mod.upload-script") && project.project(path).plugins.hasPlugin("iridium.mod.upload-script")) {
            val otherExtension = project.project(path).extensions["modrinthConfig"] as UploadScriptExtension

            (project.extensions["modrinthConfig"] as UploadScriptExtension).modrinthDependency(otherExtension.modrinthId()!!, dependencyType)
        }

        return project.dependencies.project(path, configuration = "namedElements")
    }
}