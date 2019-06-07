package com.rubyhuntersky.interaction.core.wish

import io.reactivex.Observable

interface Djinn<Params : Any, Result : Any> {
    val paramsClass: Class<Params>
    fun toObservable(params: Params): Observable<Result>
}