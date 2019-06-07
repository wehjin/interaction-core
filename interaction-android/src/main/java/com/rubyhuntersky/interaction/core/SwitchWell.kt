package com.rubyhuntersky.interaction.core

import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.WishKind
import io.reactivex.disposables.Disposable

class SwitchWell(private val lamp: Lamp) : Well {

    private val disposables = mutableMapOf<String, Disposable>()

    override fun <A : Any> addWishes(wishes: List<Wish<*, A>>, interaction: Interaction<*, A>) {
        wishes.forEach { wish ->
            val name = "${interaction.hashCode()}/${wish.name}"
            disposables.remove(name)?.dispose()
            when (wish.kind) {

                is WishKind.None<A> -> Unit

                is WishKind.One<*, A> -> {
                    val single = lamp.toSingle(wish.params, wish.kind)
                    disposables[name] = single.subscribe { action ->
                        disposables.remove(name)?.dispose()
                        interaction.sendAction(action)
                    }
                }

                is WishKind.Many<*, A> -> {
                    val toObservable = lamp.toObservable(wish.params, wish.kind)
                    disposables[name] = toObservable.subscribe(
                        { action ->
                            interaction.sendAction(action)
                        }, { throwable ->
                            throw throwable
                        }, {
                            disposables.remove(name)?.dispose()
                        })
                }
            }
        }
    }

}