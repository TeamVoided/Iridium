package org.teamvoided.iridium.mod

object ModConfigurationMutations {
    private val transmutations: MutableMap<String, List<Mutation>> = mutableMapOf()
    fun transmutations() = transmutations
    fun addMutation(projectId: String, value: Mutation) {
        val list = transmutations[projectId]
        if (list != null) {
            transmutations[projectId] = (list + value)
            return
        }

        transmutations[projectId] = listOf(value)
    }

    fun applyMutations(projectId: String, modConfiguration: ModConfiguration) {
        transmutations[projectId]?.forEach {
            it.mutate(modConfiguration)
        }
    }

    fun interface Mutation {
        fun mutate(modConfiguration: ModConfiguration)
    }
}