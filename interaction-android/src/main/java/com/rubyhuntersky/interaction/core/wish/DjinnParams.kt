package com.rubyhuntersky.interaction.core.wish

interface DjinnParams<Result : Any> {

    fun <Params : DjinnParams<Result>, Action : Any> toWish(
        name: String,
        onResult: (Result) -> Action,
        onError: (Throwable) -> Action
    ): Wish<Params, Action> {
        @Suppress("UNCHECKED_CAST") val params = this as Params
        return Wish(
            name,
            params,
            WishKind.Many(onResult, onError)
        )
    }
}