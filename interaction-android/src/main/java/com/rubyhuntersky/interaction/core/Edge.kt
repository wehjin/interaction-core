package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

open class Edge {

    private var nextKey = 1L
    private val interactions = mutableMapOf<Long, Interaction<*, *>>()
    private val disposables = mutableMapOf<Long, Disposable>()
    internal val well: Well = SwitchWell()

    open fun <V, A : Any, AFinal : Any> wish(
        name: String, interaction: Interaction<V, A>, startAction: A? = null, mapper: (V) -> AFinal
    ): Wish<AFinal> {
        return interaction.ending
            .doOnSubscribe {
                presentInteraction(interaction)
                startAction?.let { interaction.sendAction(it) }
            }
            .toWish(name, mapper, { throw it })
    }

    open fun presentInteraction(interaction: Interaction<*, *>): Long {
        return addInteraction(interaction)
    }

    fun addInteraction(interaction: Interaction<*, *>, key: Long = nextKey++): Long {
        println("Edge adding key: $key")
        interaction.edge = this
        interactions[key] = interaction
        disposables[key] = interaction.visions
            .doOnComplete { removeInteraction(key) }
            .doOnNext {
                if (interaction.isEnding(it)) {
                    removeInteraction(key)
                }
            }
            .subscribe()
        return key
    }

    operator fun plusAssign(interaction: Interaction<*, *>) {
        addInteraction(interaction)
    }

    private fun removeInteraction(key: Long) {
        println("Edge removing key: $key")
        interactions.remove(key)
        disposables.remove(key)
    }

    fun <V : Any, A : Any> findInteraction(search: InteractionSearch): Interaction<V, A> {
        val interaction = when (search) {
            is InteractionSearch.ByKey ->
                interactions[search.key]!!
            is InteractionSearch.ByName ->
                interactions.entries.find { it.value.group == search.name }!!.value
        }
        @Suppress("UNCHECKED_CAST")
        return interaction as Interaction<V, A>
    }
}