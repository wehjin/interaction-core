package com.rubyhuntersky.interaction.core

import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.WishKind
import io.reactivex.Single
import io.reactivex.disposables.Disposable

open class Edge {

    private var nextKey = 1L
    private val interactions = mutableMapOf<Long, Interaction<*, *>>()
    private val disposables = mutableMapOf<Long, Disposable>()
    val lamp = Lamp()
    internal val well: Well = SwitchWell(lamp)

    open fun <V : Any, A : Any, AFinal : Any> wish(
        name: String, interaction: Interaction<V, A>, startAction: A? = null, endVisionToAction: (V) -> AFinal
    ): Wish<Interaction<V, A>, AFinal> {
        lamp.add(object : Genie<Interaction<V, A>, V> {
            override val paramsClass: Class<Interaction<V, A>> = interaction.javaClass

            override fun toSingle(params: Interaction<V, A>): Single<V> =
                params.ending
                    .doOnSubscribe {
                        presentInteraction(interaction)
                        startAction?.let { interaction.sendAction(it) }
                    }
        })
        return Wish(name, interaction, WishKind.One(endVisionToAction, { throw it }))
    }

    open fun <V : Any, A : Any> presentInteraction(interaction: Interaction<V, A>): Long {
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

    companion object {
        const val NULL_KEY: Long = 0L
    }
}