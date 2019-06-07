package com.rubyhuntersky.interaction.core.wish

import io.reactivex.Single

interface Genie<Params : Any, Result : Any> {
    val paramsClass: Class<Params>
    fun toSingle(params: Params): Single<Result>
}
