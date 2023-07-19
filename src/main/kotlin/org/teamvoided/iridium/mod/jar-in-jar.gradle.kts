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

        val resourceDir = buildDir.resolve("resources/main")
        Config.modules.forEach {
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

    val cleanupIncludes = create("cleanupJarIncludes") {
        doFirst {
            JarHelper.deleteJarIncludes(project)
        }
    }

    jar {
        Config.modules.forEach {
            dependsOn(":$it:remapJar")
        }

        dependsOn(copyJars)
    }

    remapJar {
        finalizedBy(cleanupIncludes)
    }
}

fun DependencyHandler.modProject(path: String) = project(path, configuration = "namedElements")