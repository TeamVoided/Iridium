package org.teamvoided.iridium.project

import org.gradle.api.Project
import java.util.*

open class PublishScriptExtension(val project: Project) {
    private var releaseRepository: Pair<String, String>? = null
    private val publications: LinkedList<GradlePublication> = LinkedList()
    var publishSources: Boolean = false
    var publishJavadoc: Boolean = false

    fun releaseRepository() = releaseRepository
    fun releaseRepository(name: String, url: String) {
        releaseRepository = Pair(name, url)
    }

    fun publications() = publications
    fun publication(name: String, isSnapshot: Boolean) {
        publications.add(GradlePublication(name, isSnapshot))
    }

    data class GradlePublication(val name: String, val isSnapshot: Boolean)
}