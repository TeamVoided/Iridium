package org.teamvoided.iridium

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.teamvoided.iridium.config.IridiumLoader

class IridiumPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        println("This project is using Iridium a version independent minecraft kotlin build utility")
        IridiumLoader.loadFrom(project.projectDir.resolve("gradle/iridium/iridium.json5").absoluteFile)
    }
}