package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

class SwitchWell : Well {

    private val disposables = mutableMapOf<String, Disposable>()

    override fun <A : Any> addWishes(wishes: List<Wish<A>>, interaction: Interaction<*, A>) {
        wishes.forEach { wish ->
            val name = toName(wish, interaction)
            disposables.remove(name)?.dispose()
            when (wish) {
                is Wish.None -> Unit
                is Wish.One -> {
                    disposables[name] = wish.action.subscribe { action ->
                        disposables.remove(name)?.dispose()
                        interaction.sendAction(action)
                    }
                }
                is Wish.Many -> {
                    disposables[name] = wish.actions.subscribe(
                        { action -> interaction.sendAction(action) },
                        { IllegalStateException(it) },
                        { disposables.remove(name)?.dispose() })
                }
            }
        }
    }

    private fun toName(wish: Wish<*>, interaction: Interaction<*, *>): String = "${interaction.hashCode()}/${wish.name}"
}