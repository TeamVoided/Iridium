package org.teamvoided.iridium.project

import org.gradle.api.Project

open class PublishScriptExtension(val project: Project) {
    private val repositories: LinkedHashMap<String, String> = linkedMapOf()
    private var publicationName = project.name

    fun repositories() = repositories
    fun repository(name: String, url: String) {
        repositories[name] = url
    }

    fun publicationName() = publicationName
    fun publicationName(publicationName: String) {
        this.publicationName = publicationName
    }
}