package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

class SwitchWell : Well {

    private val wishDisposables = mutableMapOf<String, Disposable>()

    override fun <A> addWishes(wishes: List<Wish<A>>, interaction: Interaction<*, A>) {
        wishes.forEach { wish ->
            val name = wish.name
            if (name == null) {
                wish.action.subscribe(interaction::sendAction)
            } else {
                wishDisposables.remove(name)?.dispose()
                wishDisposables[name] = wish.action.subscribe(interaction::sendAction)
            }
        }
    }
}