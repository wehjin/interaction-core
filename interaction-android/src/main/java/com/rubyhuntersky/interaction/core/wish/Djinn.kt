package com.rubyhuntersky.interaction.core.wish

import io.reactivex.Observable

interface Djinn<Params : Any, Result : Any> {

    fun <Action : Any> wish(
        name: String,
        params: Params,
        resultToAction: (Result) -> Action,
        errorToAction: (Throwable) -> Action
    ): Wish<Params, Action> = Wish(name, params, WishKind.Many(resultToAction, errorToAction))

    val paramsClass: Class<Params>
    fun toObservable(params: Params): Observable<Result>
}