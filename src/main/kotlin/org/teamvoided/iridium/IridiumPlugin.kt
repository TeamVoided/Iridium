package org.teamvoided.iridium

import net.fabricmc.loom.LoomGradlePlugin
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.teamvoided.iridium.config.Config
import org.teamvoided.iridium.config.IridiumLoader
import org.teamvoided.iridium.helper.DependencyHelper
import org.teamvoided.iridium.mod.BuildScriptExtension
import org.teamvoided.iridium.mod.BuildScriptPlugin
import java.io.File

class IridiumPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("This project is using Iridium a Minecraft version-independent MC-Kotlin build utility!!!")

        IridiumLoader.loadFrom(project.projectDir.resolve("gradle/iridium/iridium").absoluteFile, true)

        loadDotEnv(project)

        project.evaluationDependsOnChildren()

        project.plugins.apply(LoomGradlePlugin::class)
        project.plugins.apply(BuildScriptPlugin::class)

        /*
        project.extensions.getByType(LoomGradleExtensionAPI::class).run {
            runs {
                create("testClient") {
                    client()
                }

                create("testServer") {
                    server()
                }
            }
        }
         */

        project.extensions.getByType(BuildScriptExtension::class).isModParent(true)

        project.tasks.withType(RemapJarTask::class.java) {
            addNestedDependencies.set(true)
        }

        project.afterEvaluate {
            Config.modules.forEach { module ->
                DependencyHelper.jarInclude(project, module)
            }
        }
    }
}

fun loadDotEnv(project: Project) {
    val dotenvFile = File("${project.projectDir}/.env")
    if (dotenvFile.exists()) {
        var count = 0
        dotenvFile.forEachLine { line ->
            val (key, value) = line.split("=", limit = 2)
            if (key.isNotBlank() && value.isNotBlank()) {
                System.setProperty(key, value)
                count++
            }
        }
        println("Loaded $count variables from .env!")
    } else println("No .env file found! No variables to load")
}
