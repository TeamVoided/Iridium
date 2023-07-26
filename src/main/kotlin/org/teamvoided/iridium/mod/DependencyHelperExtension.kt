package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.teamvoided.iridium.helper.DependencyHelper

open class DependencyHelperExtension(val project: Project) {
    fun modProject(path: String, dependencyType: DependencyType = DependencyType.REQUIRED): ProjectDependency {
        return DependencyHelper.modProject(project, path, dependencyType)
    }


    fun jarInclude(path: String, dependencyType: DependencyType = DependencyType.EMBEDDED): ProjectDependency {
        return DependencyHelper.jarInclude(project, path, dependencyType)
    }
}