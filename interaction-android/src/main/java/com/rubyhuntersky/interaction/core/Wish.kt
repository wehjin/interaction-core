package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.Single

sealed class Wish<A> {

    abstract val name: String

    data class None(
        override val name: String
    ) : Wish<Void>()

    data class One<A>(
        val action: Single<A>,
        override val name: String = action.hashCode().toString()
    ) : Wish<A>()

    data class Many<A>(
        val actions: Observable<A>,
        override val name: String
    ) : Wish<A>()
}


fun <T, A> Single<T>.toWish(name: String, onSuccess: (T) -> A, onFailure: (Throwable) -> A): Wish.One<A> {
    val action = map(onSuccess).onErrorReturn(onFailure)
    return Wish.One(action, name)
}

fun <T, A> Observable<T>.toWish(name: String, onNext: (T) -> A, onError: (Throwable) -> A): Wish.Many<A> {
    val actions = map(onNext).onErrorReturn(onError)
    return Wish.Many(actions, name)
}

fun <VSub, ASuper> Interaction<VSub, *>.toWish(name: String, mapper: (VSub) -> ASuper): Wish.One<ASuper> {
    val visionEndings = ending
    return visionEndings.toWish(name, mapper, { throw  it })
}
