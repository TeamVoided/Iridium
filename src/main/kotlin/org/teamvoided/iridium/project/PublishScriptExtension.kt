package org.teamvoided.iridium.project

import org.gradle.api.Project
import java.util.LinkedList

open class PublishScriptExtension(val project: Project) {
    private var releaseRepository: Pair<String, String>? = null
    private val publications: LinkedList<GradlePublication> = LinkedList()
    private var publishSources: Boolean = false
    private var publishJavadoc: Boolean = false

    fun releaseRepository() = releaseRepository
    fun releaseRepository(name: String, url: String) {
        releaseRepository = Pair(name, url)
    }

    fun publications() = publications
    fun publication(name: String, isSnapshot: Boolean) {
        publications += GradlePublication(name, isSnapshot)
    }

    fun publishSources() = publishSources
    fun publishSources(publishSources: Boolean) {
        this.publishSources = publishSources
    }

    fun publishJavadoc() = publishJavadoc
    fun publishJavadoc(publishJavadoc: Boolean) {
        this.publishJavadoc = publishJavadoc
    }

    data class GradlePublication(
        val name: String,
        val isSnapshot: Boolean,
    )
}