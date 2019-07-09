package com.rubyhuntersky.interaction.core.wish

interface GenieParams<Result : Any> {

    fun <Params : GenieParams<Result>, Action : Any> toWish(
        name: String,
        onResult: (Result) -> Action,
        onError: (Throwable) -> Action
    ): Wish<Params, Action> {
        @Suppress("UNCHECKED_CAST") val params = this as Params
        return Wish(
            name,
            params,
            WishKind.One(onResult, onError)
        )
    }
}