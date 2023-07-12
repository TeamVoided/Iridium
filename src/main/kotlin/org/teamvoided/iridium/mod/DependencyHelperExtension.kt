package org.teamvoided.iridium.mod

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.project

open class DependencyHelperExtension(val project: Project) {
    fun modProject(path: String): ProjectDependency = project.dependencies.project(path, configuration = "namedElements")
}