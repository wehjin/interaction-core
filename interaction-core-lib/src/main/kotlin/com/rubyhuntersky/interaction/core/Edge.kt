package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

open class Edge {

    private var nextKey = 1L
    private val interactions = mutableMapOf<Long, Interaction<*, *>>()
    private val disposables = mutableMapOf<Long, Disposable>()

    fun presentInteraction(interaction: Interaction<*, *>): Long = addInteraction(interaction)

    fun addInteraction(interaction: Interaction<*, *>): Long {
        val key = nextKey++
        interactions[key] = interaction
        disposables[key] = interaction.visionStream.doOnComplete { removeInteraction(key) }.subscribe()
        return key
    }

    private fun removeInteraction(key: Long) {
        interactions.remove(key)
        disposables.remove(key)
    }

    fun <V : Any, A : Any, R : Any> findInteraction(search: InteractionSearch): Interaction<V, A> {
        val interaction = when (search) {
            is InteractionSearch.ByKey ->
                interactions[search.key]!!
            is InteractionSearch.ByName ->
                interactions.entries.find { it.value.name == search.name }!!.value
        }
        @Suppress("UNCHECKED_CAST")
        return interaction as Interaction<V, A>
    }
}