package com.rubyhuntersky.vx

import io.reactivex.Observable


typealias DashView<Content, Event> = Dash.View<Content, Event>
typealias DashLatitude = Dash.Latitude

interface Dash<Content : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Content, Event>

    interface View<Content : Any, Event : Any> {
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
        fun setContent(content: Content)
        val events: Observable<Event>
    }

    data class Latitude(val height: Int)
}

data class HBound(val start: Int, val end: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun startZero(): HBound = HBound(0, end - start)
}

data class ViewId(val markers: List<Int> = emptyList()) {
    fun extend(marker: Int): ViewId = ViewId(markers.toMutableList().also { it.add(marker) })
}

interface ViewHost {
    fun addTextLine(id: ViewId): DashView<TextLine, Nothing>
    fun addInput(id: ViewId): Dash.View<Input, Nothing>
}


fun <CoreC : Any, EdgeC : Any, Ev : Any> Dash<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Dash<EdgeC, Ev> {
    return object : Dash<EdgeC, Ev> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<EdgeC, Ev> =
            this@transform.enview(viewHost, id).transform(transformer)
    }
}

fun <CoreC : Any, EdgeC : Any, Ev : Any> DashView<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): DashView<EdgeC, Ev> {
    return object : Dash.View<EdgeC, Ev> {
        override fun setHBound(hbound: HBound) = this@transform.setHBound(hbound)
        override val latitudes: Observable<Dash.Latitude> get() = this@transform.latitudes
        override fun setAnchor(anchor: Anchor) = this@transform.setAnchor(anchor)
        override fun setContent(content: EdgeC) = this@transform.setContent(transformer(content))
        override val events: Observable<Ev> get() = this@transform.events.map { it }
    }
}

data class VBound(val ceiling: Int, val floor: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)
}
