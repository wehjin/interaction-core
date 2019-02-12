package com.rubyhuntersky.vx.additions

import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.ViewHost
import com.rubyhuntersky.vx.ViewId
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject


data class Floor<A : Any, B : Any, C : Any, EvA : Any, EvB : Any, EvC>(
    val dash: Dash<B, EvB>,
    val onContent: (C) -> Pair<A, B>,
    val onCeilingEvent: ((EvA) -> EvC),
    val onFloorEvent: ((EvB) -> EvC)
) {
    fun convertTop(events: Observable<EvA>): Observable<EvC> = convert(onCeilingEvent, events)
    fun convertBottom(events: Observable<EvB>): Observable<EvC> = convert(onFloorEvent, events)

    private fun <From, To> convert(convert: ((From) -> To)?, events: Observable<From>): Observable<To> {
        return convert?.let { events.map { convert(it) } } ?: Observable.never()
    }
}

operator fun <A : Any, B : Any, C : Any, EvA : Any, EvB : Any, EvC : Any> Dash<A, EvA>.plus(floor: Floor<A, B, C, EvA, EvB, EvC>): Dash<C, EvC> =
    object : Dash<C, EvC> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<C, EvC> = object : Dash.View<C, EvC> {
            private val viewA = this@plus.enview(viewHost, id.extend(0))
            private val viewB = floor.dash.enview(viewHost, id.extend(1))
            private val heights = Observable.combineLatest(viewA.latitudes, viewB.latitudes, sumLatitudes)
            private val anchorBehavior = BehaviorSubject.createDefault(Anchor(0, 0f))
            private val sizeAnchors = Observable.combineLatest(heights, anchorBehavior, toSizeAnchor)
            private val composite = CompositeDisposable()

            init {
                sizeAnchors.distinctUntilChanged()
                    .subscribe { sizeAnchor ->
                        val ceilFloor = sizeAnchor.second.toBounds(sizeAnchor.first)
                        viewA.setAnchor(Anchor(ceilFloor.first, 0f))
                        viewB.setAnchor(Anchor(ceilFloor.second, 1f))
                    }.addTo(composite)
            }

            override fun setLimit(limit: Dash.Limit) {
                viewA.setLimit(limit)
                viewB.setLimit(limit)
            }

            override val latitudes: Observable<Dash.Latitude> get() = heights.map { Dash.Latitude(it) }

            override fun setAnchor(anchor: Anchor) {
                anchorBehavior.onNext(anchor)
            }

            override fun setContent(content: C) {
                val ab = floor.onContent(content)
                viewA.setContent(ab.first)
                viewB.setContent(ab.second)
            }

            override val events: Observable<EvC>
                get() = floor.convertTop(viewA.events).mergeWith(floor.convertBottom(viewB.events))
        }
    }

private val toSizeAnchor = BiFunction<Int, Anchor, Pair<Int, Anchor>>(::Pair)
private val sumLatitudes = BiFunction<Dash.Latitude, Dash.Latitude, Int> { a, b -> a.height + b.height }
