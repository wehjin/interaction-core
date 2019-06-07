package com.rubyhuntersky.interaction.core.wish.interval

import com.rubyhuntersky.interaction.core.wish.Djinn
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.WishKind
import io.reactivex.Observable

object IntervalDjinn : Djinn<Interval, Long> {

    override val paramsClass: Class<Interval> = Interval::class.java
    override fun toObservable(params: Interval): Observable<Long> =
        Observable.interval(params.period, params.timeUnit)

    fun <Action : Any> newWish(
        name: String, interval: Interval, indexToAction: (index: Long) -> Action
    ): Wish<Interval, Action> {
        return Wish(name, interval, WishKind.Many(indexToAction, { throw it }))
    }
}