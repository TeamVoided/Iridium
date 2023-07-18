package org.teamvoided.iridium.mod

import org.teamvoided.iridium.config.Config
import org.teamvoided.iridium.helper.JarHelper

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

dependencies {
    Config.modules.forEach {
        implementation(modProject(":$it"))
    }
}

val bse = project.extensions["modSettings"] as BuildScriptExtension
bse.isModParent(true)

tasks {
    val copyJars = create("copyJars") {
        val modJars = mutableListOf<File>()
        val resourceJarDir = buildDir.resolve("resources/main/META-INF/jars")

        val resourceDir = buildDir.resolve("resources/main")
        Config.modules.forEach {
            val task = getByPath(":$it:remapJar")
            dependsOn(task)
            modJars += JarHelper.computeDestJarPath(project(":$it"), project)
        }

        ModConfigurationMutations.addMutation(project.name) { it ->
            it.jars += modJars.map {
                ModConfiguration.JarFile(JarHelper.toMetaInfJarString(it, resourceDir))
            }
        }

        outputs.files(modJars)

        doFirst {
            Config.modules.forEach {
                JarHelper.copyJar(project(":$it"), project)
            }
        }
    }

    remapJar {
        Config.modules.forEach {
            dependsOn(":$it:remapJar")
        }

        dependsOn(copyJars)
    }
}

fun DependencyHandler.modProject(path: String) = project(path, configuration = "namedElements")