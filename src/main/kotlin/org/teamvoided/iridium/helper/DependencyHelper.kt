package org.teamvoided.iridium.helper

import com.modrinth.minotaur.dependencies.DependencyType
import net.fabricmc.loom.util.Pair
import net.fabricmc.loom.util.ZipUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskInputs
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

        val destJarPath = JarHelper.computeDestJarPath(project.project(":$path"), project, project.gradle.startParameter.taskNames.contains("remapJar"))


        project.tasks.create("copyJarFrom${path.replace(":", ".")}") {
            if (project.gradle.startParameter.taskNames.contains("remapJar")) {
                val oRJT = project.project(path).tasks.getByPath("remapJar")
                dependsOn(oRJT)
                project.project(path).tasks.filter { Regex("copyJarFrom.*").matches(it.name) }.forEach { dependsOn(it) }

                val remapJarTask = project.tasks.getByName("remapJar")
                remapJarTask.finalizedBy(this)

                doFirst {
                    val oJar = oRJT.outputs.files.singleFile
                    ZipUtils.add(remapJarTask.outputs.files.singleFile.toPath(), listOf(Pair("META-INF/jars/${oJar.name}", oJar.readBytes())))
                }
            } else {
                val otherJarTask = project.project(path).tasks.getByPath("jar")
                dependsOn(otherJarTask)
                project.project(path).tasks.filter { Regex("copyJarFrom.*").matches(it.name) }.forEach { dependsOn(it) }

                val jarTask = project.tasks.getByName("jar")
                jarTask.finalizedBy(this)

                doFirst {
                    val oJar = otherJarTask.outputs.files.singleFile
                    ZipUtils.add(jarTask.outputs.files.singleFile.toPath(), listOf(Pair("META-INF/jars/${oJar.name}", oJar.readBytes())))
                }
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

    private fun <T> extracted(collection: Iterable<T>, str2: StringBuilder) {
        for (dep in collection) {
            str2.append("${if (dep is Task) dep.path else if (dep is TaskInputs) dep.files.toString() else dep.toString()}, ")
        }

        str2.setLength(str2.length - 2)
        str2.append(" ]")
    }
}