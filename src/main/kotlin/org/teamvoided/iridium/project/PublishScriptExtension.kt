package org.teamvoided.iridium.project

open class PublishScriptExtension {
    private val repositories: LinkedHashMap<String, String> = linkedMapOf()

    fun repositories() = repositories
    fun repository(name: String, url: String) {
        repositories[name] = url
    }
}