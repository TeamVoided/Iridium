package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.project
import org.teamvoided.iridium.helper.JarHelper

open class DependencyHelperExtension(val project: Project, val buildScriptExtension: BuildScriptExtension) {
    fun modProject(path: String, dependencyType: DependencyType = DependencyType.REQUIRED): ProjectDependency {
        addModrinthDependency(path, dependencyType)

        return project.dependencies.project(path, configuration = "namedElements")
    }


    fun jarInclude(path: String, dependencyType: DependencyType = DependencyType.EMBEDDED): ProjectDependency {
        project.evaluationDependsOn(":$path")

        addModrinthDependency(path, dependencyType)

        val destJarPath = JarHelper.computeDestJarPath(project.project(":$path"), project)
        val copyTask = project.tasks.create("copyJarFrom${path.replace(":", ".")}") {
            outputs.file(destJarPath)

            doFirst {
                JarHelper.deleteJarIncludes(project)
                JarHelper.copyJar(project.project(":$path"), project)
            }
        }

        project.tasks.getByName("remapJar") {
            dependsOn(copyTask)
        }

        buildScriptExtension.mutation {
            this.jars += ModConfiguration.JarFile(JarHelper.toMetaInfJarString(destJarPath, JarHelper.getResourceDir(project)))
        }

        return project.dependencies.project(path, configuration = "namedElements")
    }

    protected fun addModrinthDependency(path: String, dependencyType: DependencyType) {
        if (project.plugins.hasPlugin("iridium.mod.upload-script") && project.project(path).plugins.hasPlugin("iridium.mod.upload-script")) {
            val otherExtension = project.project(path).extensions["modrinthConfig"] as UploadScriptExtension

            (project.extensions["modrinthConfig"] as UploadScriptExtension).modrinthDependency(otherExtension.modrinthId()!!, dependencyType)
        }
    }
}