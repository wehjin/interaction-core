package com.rubyhuntersky.interaction.core.wish

import com.rubyhuntersky.interaction.core.wish.interval.Intervals
import io.reactivex.Observable
import io.reactivex.Single

class Lamp {

    private val genies: MutableMap<Class<*>, Genie<*, *>> = mutableMapOf()
    private val djinns: MutableMap<Class<*>, Djinn<*, *>> = mutableMapOf()
    private val paramsClasses: MutableSet<Class<*>> = mutableSetOf()
    private val classLookup: MutableMap<Class<*>, Class<*>> = mutableMapOf()

    fun <Params : Any, Result : Any> add(genie: Genie<Params, Result>): Lamp {
        return this.also {
            val paramsClass = genie.paramsClass
            paramsClasses.add(paramsClass)
            classLookup[paramsClass] = paramsClass
            genies[paramsClass] = genie
        }
    }

    fun <Params : Any, Result : Any> add(djinn: Djinn<Params, Result>): Lamp {
        return this.also {
            val paramsClass = djinn.paramsClass
            paramsClasses.add(paramsClass)
            classLookup[djinn.paramsClass] = djinn.paramsClass
            djinns[djinn.paramsClass] = djinn
        }
    }

    init {
        add(Intervals.DJINN)
    }

    fun <Params : Any, Result : Any, Action : Any> toSingle(
        params: Params, one: WishKind.One<Result, Action>
    ): Single<Action> {
        @Suppress("UNCHECKED_CAST")
        val genie = genies[lookup(params)] as Genie<Params, Result>
        return genie.toSingle(params).map(one.resultToAction).onErrorReturn(one.errorToAction)
    }

    fun <Params : GenieParams<Result>, Result : Any, Action : Any> toSingle(wish: Wish<Params, Action>): Single<Action> {
        @Suppress("UNCHECKED_CAST") val one = wish.kind as WishKind.One<Result, Action>
        return toSingle(wish.params, one)
    }

    private fun lookup(find: Any): Class<*> {
        return classLookup[find::class.java]
            ?.let { it }
            ?: paramsClasses
                .first {
                    it.isInstance(find)
                }
                .also {
                    classLookup[find::class.java] = it
                }
    }

    fun <Params : Any, Result : Any, Action : Any> toObservable(
        params: Params, many: WishKind.Many<Result, Action>
    ): Observable<Action> {
        @Suppress("UNCHECKED_CAST")
        val genie = djinns[lookup(params)] as Djinn<Params, Result>
        return genie.toObservable(params).map(many.resultToAction).onErrorReturn(many.errorToAction)
    }
}


