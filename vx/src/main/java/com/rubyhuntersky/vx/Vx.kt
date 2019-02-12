package com.rubyhuntersky.vx

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


fun <CoreC : Any, Event : Any, EdgeC : Any> Dash<CoreC, Event>.transform(transformer: (EdgeC) -> CoreC): Dash<EdgeC, Event> {
    return object : Dash<EdgeC, Event> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<EdgeC, Event> =
            this@transform.enview(viewHost, id).transform(transformer)
    }
}

fun <CoreC : Any, Event : Any, EdgeC : Any> DashView<CoreC, Event>.transform(transformer: (EdgeC) -> CoreC): DashView<EdgeC, Event> {
    return object : Dash.View<EdgeC, Event> {
        override fun setLimit(limit: Dash.Limit) = this@transform.setLimit(limit)
        override val latitudes: Observable<Dash.Latitude> get() = this@transform.latitudes
        override fun setAnchor(anchor: Anchor) = this@transform.setAnchor(anchor)
        override fun setContent(content: EdgeC) = this@transform.setContent(transformer(content))
        override val events: Observable<Event> get() = this@transform.events.map { it }
    }
}
