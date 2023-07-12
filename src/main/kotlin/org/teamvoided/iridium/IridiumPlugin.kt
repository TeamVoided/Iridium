package org.teamvoided.iridium

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.teamvoided.iridium.config.IridiumLoader

class IridiumPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        IridiumLoader.loadFrom(project.projectDir.resolve("gradle/iridium/iridium.json5").absoluteFile)
    }
}