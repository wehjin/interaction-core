package com.rubyhuntersky.interaction.core.wish.interval

import com.rubyhuntersky.interaction.core.wish.Djinn
import com.rubyhuntersky.interaction.core.wish.DjinnParams
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

data class Intervals(
    val period: Long,
    val timeUnit: TimeUnit
) : DjinnParams<Long> {

    companion object {
        val DJINN = object : Djinn<Intervals, Long> {
            override val paramsClass: Class<Intervals> = Intervals::class.java
            override fun toObservable(params: Intervals): Observable<Long> {
                return Observable.interval(params.period, params.timeUnit)
            }
        }
    }
}