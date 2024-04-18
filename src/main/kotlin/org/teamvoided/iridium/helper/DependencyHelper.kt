package org.teamvoided.iridium.helper

import com.modrinth.minotaur.dependencies.DependencyType
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskInputs
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.project
import org.teamvoided.iridium.mod.UploadScriptExtension

object DependencyHelper {
    fun modProject(project: Project, path: String, dependencyType: DependencyType = DependencyType.REQUIRED): ProjectDependency {
        addModrinthDependency(project, path, dependencyType)

        return project.dependencies.project(path, configuration = "namedElements")
    }

    fun addModrinthDependency(project: Project, path: String, dependencyType: DependencyType) {
        if (project.plugins.hasPlugin("iridium.mod.upload-script") && project.project(path).plugins.hasPlugin("iridium.mod.upload-script")) {
            val otherExtension = project.project(path).extensions["modrinthConfig"] as UploadScriptExtension

            (project.extensions["modrinthConfig"] as UploadScriptExtension).modrinthDependency(otherExtension.modrinthId!!, dependencyType)
        }
    }

    fun jarInclude(project: Project, path: String, dependencyType: DependencyType = DependencyType.EMBEDDED): Dependency {
        addModrinthDependency(project, path, dependencyType)
        val projectJar = project.project(":$path").tasks.getByName("remapJar").outputs.files.singleFile

        project.tasks.getByName("remapJar") {
            this as RemapJarTask
            nestedJars.from(projectJar)
        }

        return project.dependencies.add("implementation", project.dependencies.project("$path:", "namedElements"))!!
    }

    private fun <T> extracted(collection: Iterable<T>, str2: StringBuilder) {
        for (dep in collection) {
            str2.append("${if (dep is Task) dep.path else if (dep is TaskInputs) dep.files.toString() else dep.toString()}, ")
        }

        str2.setLength(str2.length - 2)
        str2.append(" ]")
    }
}