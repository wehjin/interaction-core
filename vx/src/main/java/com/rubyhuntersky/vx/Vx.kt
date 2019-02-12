package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.additions.Floor
import com.rubyhuntersky.vx.additions.plus
import io.reactivex.Observable


typealias DashView<Content, Event> = Dash.View<Content, Event>
typealias DashLimit = Dash.Limit
typealias DashLatitude = Dash.Latitude

interface Dash<Content : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Content, Event>

    interface View<Content : Any, Event : Any> {
        fun setLimit(limit: Limit)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
        fun setContent(content: Content)
        val events: Observable<Event>
    }

    data class Limit(val start: Int, val end: Int)
    data class Latitude(val height: Int)
}

data class ViewId(val markers: List<Int> = emptyList()) {
    fun extend(marker: Int): ViewId = ViewId(markers.toMutableList().also { it.add(marker) })
}

interface ViewHost {
    fun addTextLine(id: ViewId): DashView<TextLine, Nothing>
}


fun <CoreC : Any, EdgeC : Any, Ev : Any> Dash<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Dash<EdgeC, Ev> {
    return object : Dash<EdgeC, Ev> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<EdgeC, Ev> =
            this@transform.enview(viewHost, id).transform(transformer)
    }
}

fun <CoreC : Any, EdgeC : Any, Ev : Any> DashView<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): DashView<EdgeC, Ev> {
    return object : Dash.View<EdgeC, Ev> {
        override fun setLimit(limit: Dash.Limit) = this@transform.setLimit(limit)
        override val latitudes: Observable<Dash.Latitude> get() = this@transform.latitudes
        override fun setAnchor(anchor: Anchor) = this@transform.setAnchor(anchor)
        override fun setContent(content: EdgeC) = this@transform.setContent(transformer(content))
        override val events: Observable<Ev> get() = this@transform.events.map { it }
    }
}


data class FloorMerge<A : Any, B : Any, C : Any, Ev : Any>(
    val floorDash: Dash<B, Ev>,
    val onContent: (C) -> Pair<A, B>
)

operator fun <A : Any, B : Any, C : Any, Ev : Any> Dash<A, Ev>.plus(seam: FloorMerge<A, B, C, Ev>): Dash<C, Ev> =
    this + Floor(seam.floorDash, seam.onContent, ::identity, ::identity)

fun <T> identity(t: T): T = t


