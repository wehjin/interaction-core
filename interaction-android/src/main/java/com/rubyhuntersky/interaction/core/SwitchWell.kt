package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

class SwitchWell : Well {

    private val wishDisposables = mutableMapOf<String, Disposable>()

    override fun <A> addWishes(wishes: List<Wish<A>>, interaction: Interaction<*, A>) {
        wishes.forEach { wish ->
            val name = wish.name
            if (name == null) {
                wish.action.subscribe { action ->
                    fulfillWish(interaction, action)
                }
            } else {
                wishDisposables.remove(name)?.dispose()
                wishDisposables[name] = wish.action.subscribe { action ->
                    wishDisposables.remove(name)
                    fulfillWish(interaction, action)
                }
            }
        }
    }

    private fun <A> fulfillWish(interaction: Interaction<*, A>, action: A) {
        interaction.sendAction(action)
    }
}