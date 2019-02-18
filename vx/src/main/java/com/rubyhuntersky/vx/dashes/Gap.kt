package com.rubyhuntersky.vx.dashes

import com.rubyhuntersky.vx.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class Gap {
    data class Pixels(val count: Int) : Gap()
}

object GapDash : Dash<Gap, Nothing> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<Gap, Nothing> =
        object : Dash.View<Gap, Nothing> {
            override fun setHBound(hbound: HBound) = Unit
            override val latitudes: Observable<Dash.Latitude> get() = heightBehavior.map { Dash.Latitude(it) }
            private val heightBehavior = BehaviorSubject.create<Int>()
            override fun setAnchor(anchor: Anchor) = Unit
            override fun setContent(content: Gap) = heightBehavior.onNext((content as Gap.Pixels).count)
            override val events: Observable<Nothing> = Observable.never()
        }
}
