package com.rubyhuntersky.interaction.core.wish

import io.reactivex.Single

interface Genie<Params : Any, Result : Any> {

    fun <Action : Any> wish(
        name: String,
        params: Params,
        resultToAction: (Result) -> Action,
        errorToAction: (Throwable) -> Action
    ): Wish<Params, Action> = Wish(name, params, WishKind.One(resultToAction, errorToAction))

    val paramsClass: Class<Params>
    fun toSingle(params: Params): Single<Result>
}
