package org.teamvoided.iridium.mod

import org.teamvoided.iridium.config.Config
import java.io.FileInputStream
import java.io.FileOutputStream

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

dependencies {
    Config.modules.forEach {
        implementation(modProject(":$it"))
    }
}

tasks {
    val copyJars = create("copyJars") {
        val modJars = mutableListOf<File>()
        val resourceJarDir = buildDir.resolve("resources/main/META-INF/jars")

        val resourceDir = buildDir.resolve("resources/main")
        Config.modules.forEach {
            val task = getByPath(":$it:remapJar")
            dependsOn(task)
            modJars += task.outputs.files.singleFile
        }

        val newFiles = modJars.map {
            resourceJarDir.resolve(
                it.toString().removePrefix("$rootDir${File.separator}")
                    .replace(File.separator, "/")
                    .replace(Regex(".*?/build/libs/"), "")
            ).absoluteFile
        }
        ModConfigurationMutations.addMutation(project.name) { it ->
            it.jars += newFiles.map {
                ModConfiguration.JarFile(it.toRelativeString(resourceDir).replace(File.separator, "/"))
            }
        }

        outputs.files(newFiles)

        doFirst {
            resourceJarDir.listFiles()?.forEach {
                it.deleteRecursively()
            }

            newFiles.forEachIndexed { index, file ->
                file.parentFile.mkdirs()
                file.createNewFile()
                val data = FileInputStream(modJars[index]).use { it.readBytes() }
                FileOutputStream(file).use { it.write(data) }
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