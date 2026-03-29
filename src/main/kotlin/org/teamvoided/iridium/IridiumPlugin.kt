package org.teamvoided.iridium

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.teamvoided.iridium.config.IridiumProps
import org.teamvoided.iridium.mod.BuildScriptPlugin
import java.io.File

class IridiumPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("This project is using Iridium a Minecraft version and loader independent kotlin build utility.")
        IridiumProps.loadFrom(project.projectDir.resolve("properties").absoluteFile, true)
        loadDotEnv(project)
        project.evaluationDependsOnChildren()
        project.plugins.apply(BuildScriptPlugin::class)
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
}
