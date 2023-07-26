package org.teamvoided.iridium.helper

import com.modrinth.minotaur.dependencies.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.project
import org.teamvoided.iridium.mod.BuildScriptExtension
import org.teamvoided.iridium.mod.ModConfiguration
import org.teamvoided.iridium.mod.UploadScriptExtension

object DependencyHelper {
    fun modProject(project: Project, path: String, dependencyType: DependencyType = DependencyType.REQUIRED): ProjectDependency {
        addModrinthDependency(project, path, dependencyType)

        return project.dependencies.project(path, configuration = "namedElements")
    }

    fun addModrinthDependency(project: Project, path: String, dependencyType: DependencyType) {
        if (project.plugins.hasPlugin("iridium.mod.upload-script") && project.project(path).plugins.hasPlugin("iridium.mod.upload-script")) {
            val otherExtension = project.project(path).extensions["modrinthConfig"] as UploadScriptExtension

            (project.extensions["modrinthConfig"] as UploadScriptExtension).modrinthDependency(otherExtension.modrinthId()!!, dependencyType)
        }
    }

    fun jarInclude(project: Project, path: String, dependencyType: DependencyType = DependencyType.EMBEDDED): ProjectDependency {
        project.evaluationDependsOn(":$path")

        addModrinthDependency(project, path, dependencyType)

        val destJarPath = JarHelper.computeDestJarPath(project.project(":$path"), project)
        val cleanup = project.tasks.create("cleanupJarIncludeFor${path.replace(":", ".")}") {
            doFirst {
                JarHelper.deleteJarIncludes(project)
            }
        }

        project.tasks.create("copyJarFrom${path.replace(":", ".")}") {
            outputs.file(destJarPath)

            val jarTask = project.tasks.getByName("jar")
            val remapJarTask = project.tasks.getByName("remapJar")
            jarTask.dependsOn(this)
            remapJarTask.finalizedBy(cleanup)

            doFirst {
                JarHelper.copyJar(project.project(path), project)
            }
        }

        val buildScriptExtension = project.extensions["modSettings"] as BuildScriptExtension

        buildScriptExtension.mutation {
            this.jars += ModConfiguration.JarFile(
                JarHelper.toMetaInfJarString(
                    destJarPath,
                    JarHelper.getResourceDir(project)
                )
            )
        }

        return project.dependencies.project(path, configuration = "namedElements")
    }
}