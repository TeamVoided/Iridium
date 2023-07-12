package org.teamvoided.iridium.mod

open class UploadScriptExtension {
    private var modrinthId: String? = null
    private var customModrinthTokenProperty: String? = null

    fun modrinthId() = modrinthId
    fun modrinthId(modrinthId: String) { this.modrinthId = modrinthId }

    fun customModrinthTokenProperty() = customModrinthTokenProperty
    fun customModrinthTokenProperty(customModrinthTokenProperty: String) { this.customModrinthTokenProperty = customModrinthTokenProperty }
}