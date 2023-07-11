package org.teamvoided.iridium

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.teamvoided.iridium.config.IridiumLoader
import java.io.File

class IridiumPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        IridiumLoader.loadFrom(File("gradle/iridium/iridium.json5"))
    }
}