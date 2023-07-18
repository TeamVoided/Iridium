package org.teamvoided.iridium.helper

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object JarHelper {
    fun computeDestJarPath(from: Project, to: Project): File {
        val fromJar = from.tasks.getByName("remapJar").outputs.files.singleFile
        val resourceJarDir = to.buildDir.resolve("resources/main/META-INF/jars")

        return resourceJarDir.resolve(
            fromJar.toString()
                .replace(File.separator, "/")
                .replace(Regex(".*?/build/libs/"), "")
        ).absoluteFile
    }

    fun computeDestJarPath(fromJar: File, to: Project): File {
        val resourceJarDir = to.buildDir.resolve("resources/main/META-INF/jars")

        return resourceJarDir.resolve(
            fromJar.toString()
                .replace(File.separator, "/")
                .replace(Regex(".*?/build/libs/"), "")
        ).absoluteFile
    }

    fun deleteJarIncludes(from: Project) {
        from.buildDir.resolve("resources/main/META-INF/jars").listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

    fun copyJar(from: Project, to: Project): File {
        val fromJar = from.tasks.getByName("remapJar").outputs.files.singleFile
        val destJar = computeDestJarPath(fromJar, to)

        val data = FileInputStream(fromJar).use { it.readBytes() }
        FileOutputStream(destJar).use { it.write(data) }

        return destJar
    }

    fun getResourceDir(project: Project) =
        project.buildDir.resolve("resources/main")

    fun toMetaInfJarString(jarFile: File, resourceDir: File): String {
        return jarFile.toRelativeString(resourceDir).replace(File.separator, "/")
    }
}