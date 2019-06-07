package com.rubyhuntersky.interaction.core.wish

sealed class WishKind<out Action : Any> {

    class None<out Action : Any> : WishKind<Action>()

    data class One<in Result : Any, out Action : Any>(
        val resultToAction: (Result) -> Action,
        val errorToAction: (Throwable) -> Action
    ) : WishKind<Action>()

    data class Many<in Result : Any, out Action : Any>(
        val resultToAction: (Result) -> Action,
        val errorToAction: (Throwable) -> Action
    ) : WishKind<Action>()
}