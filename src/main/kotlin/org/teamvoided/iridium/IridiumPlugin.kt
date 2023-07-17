package org.teamvoided.iridium

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.teamvoided.iridium.config.IridiumLoader

class IridiumPlugin: Plugin<Project> {override fun apply(project: Project) {
        println("This project is using Iridium a Minecraft version-independent MC-Kotlin build utility!!!")

        val iridiumExtension: IridiumExtension = project.extensions.create("iridium", IridiumExtension::class.java)

        project.afterEvaluate {
            if (!iridiumExtension.dirty) {
                IridiumLoader.loadFrom(project.projectDir.resolve("gradle/iridium/iridium").absoluteFile)
            }
        }
    }
}