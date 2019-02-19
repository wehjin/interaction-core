package com.rubyhuntersky.interaction.core

object InteractionRegistry {

    private val interactions = mutableMapOf<Long, Interaction<*, *>>()

    fun <V : Any, A : Any> addInteraction(key: Long, interaction: Interaction<V, A>) {
        interactions[key] = interaction as Interaction<*, *>
    }

    fun <V : Any, A : Any> findInteraction(key: Long): Interaction<V, A>? {
        @Suppress("UNCHECKED_CAST")
        return interactions[key] as? Interaction<V, A>
    }

    fun dropInteraction(key: Long) {
        interactions.remove(key)
    }
}