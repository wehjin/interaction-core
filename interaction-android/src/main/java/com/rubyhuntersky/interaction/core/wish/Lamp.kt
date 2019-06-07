package com.rubyhuntersky.interaction.core.wish

import com.rubyhuntersky.interaction.core.wish.interval.IntervalDjinn
import io.reactivex.Observable
import io.reactivex.Single

class Lamp {

    private val genies: MutableMap<Class<*>, Genie<*, *>> = mutableMapOf()
    private val djinns: MutableMap<Class<*>, Djinn<*, *>> = mutableMapOf()

    fun <Params : Any, Result : Any> add(genie: Genie<Params, Result>): Lamp =
        this.also { genies[genie.paramsClass] = genie }

    fun <Params : Any, Result : Any> add(djinn: Djinn<Params, Result>): Lamp =
        this.also { djinns[djinn.paramsClass] = djinn }

    init {
        add(IntervalDjinn)
    }

    fun <Params : Any, Result : Any, Action : Any> toSingle(
        params: Params, one: WishKind.One<Result, Action>
    ): Single<Action> {
        @Suppress("UNCHECKED_CAST")
        val genie = genies[params::class.java] as Genie<Params, Result>
        return genie.toSingle(params).map(one.resultToAction).onErrorReturn(one.errorToAction)
    }

    fun <Params : Any, Result : Any, Action : Any> toObservable(
        params: Params, many: WishKind.Many<Result, Action>
    ): Observable<Action> {
        @Suppress("UNCHECKED_CAST")
        val genie = djinns[params::class.java] as Djinn<Params, Result>
        return genie.toObservable(params).map(many.resultToAction).onErrorReturn(many.errorToAction)
    }
}


