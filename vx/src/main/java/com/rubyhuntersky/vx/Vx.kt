package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.dashes.InputEvent
import com.rubyhuntersky.vx.dashes.InputSight
import com.rubyhuntersky.vx.dashes.TextLineSight
import io.reactivex.Observable


typealias DashView<Sight, Event> = Dash.View<Sight, Event>
typealias DashLatitude = Dash.Latitude

interface Dash<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface View<Sight : Any, Event : Any> {
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
        fun setSight(sight: Sight)
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
    fun addTextLine(id: ViewId): DashView<TextLineSight, Nothing>
    fun addInput(id: ViewId): Dash.View<InputSight, InputEvent>
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
        override fun setSight(sight: EdgeC) = this@transform.setSight(transformer(sight))
        override val events: Observable<Ev> get() = this@transform.events.map { it }
    }
}

data class VBound(val ceiling: Int, val floor: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)
}

fun <Sight : Any, Event : Any, NeverE : Any> Dash<Sight, Event>.neverEvent(): Dash<Sight, NeverE> {
    return object : Dash<Sight, NeverE> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<Sight, NeverE> {
            val coreView = this@neverEvent.enview(viewHost, id)
            return object : Dash.View<Sight, NeverE> {
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Dash.Latitude> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override val events: Observable<NeverE> get() = Observable.never()
            }
        }
    }
}
