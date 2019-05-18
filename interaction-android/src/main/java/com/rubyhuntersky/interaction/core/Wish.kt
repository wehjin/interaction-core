package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.Single

sealed class Wish<A : Any> {

    abstract val name: String

    data class None<A : Any>(
        override val name: String
    ) : Wish<A>() {
        override fun toString(): String = "Wish.None(name='$name')"
    }

    data class One<A : Any>(
        val action: Single<A>,
        override val name: String = action.hashCode().toString()
    ) : Wish<A>() {
        override fun toString(): String = "Wish.One(action=$action, name='$name')"
    }

    data class Many<A : Any>(
        val actions: Observable<A>,
        override val name: String
    ) : Wish<A>() {
        override fun toString(): String = "Wish.Many(actions=$actions, name='$name')"
    }
}


fun <T, A : Any> Single<T>.toWish(
    name: String, onSuccess: (T) -> A, onFailure: (Throwable) -> A
): Wish.One<A> {
    val action = map(onSuccess).onErrorReturn(onFailure)
    return Wish.One(action, name)
}

fun <T, A : Any> Observable<T>.toWish(
    name: String, onNext: (T) -> A, onError: (Throwable) -> A
): Wish.Many<A> {
    val actions = map(onNext).onErrorReturn(onError)
    return Wish.Many(actions, name)
}

fun <VSub, ASuper : Any> Interaction<VSub, *>.toWish(
    name: String, mapper: (VSub) -> ASuper
): Wish.One<ASuper> {
    return ending.toWish(name, mapper, { throw  it })
}
