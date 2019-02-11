package com.rubyhuntersky.vx

import io.reactivex.Observable


typealias DashView<Data, Event> = Dash.View<Data, Event>

interface Dash<Data : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Data, Event>

    interface View<Data : Any, Event : Any> {
        fun setLimit(limit: Limit)
        val latitudes: Observable<Latitude>
        fun setContent(content: Data)
        val events: Observable<Event>
    }

    data class Limit(val start: Int, val end: Int, val anchor: Anchor)
    data class Latitude(val ceiling: Int, val floor: Int)
}

data class ViewId(val markers: List<Int> = emptyList())

interface ViewHost {
    fun addTextLine(id: ViewId): DashView<TextLine, Nothing>
}
