package com.rubyhuntersky.vx

import io.reactivex.Observable

data class DashLimit(val start: Int, val end: Int, val anchor: Anchor)
data class DashLatitude(val ceiling: Int, val floor: Int)

interface Dash<Data : Any, Event : Any> {
    fun envision(viewHost: ViewHost): DashView<Data, Event>
}

interface DashView<Data : Any, Event : Any> {
    fun setLimit(dashLimit: DashLimit)
    val latitude: Observable<DashLatitude>
    fun setContent(content: Data)
    val event: Observable<Event>
}

interface ViewHost {
    fun addTextLine(id: ViewId): DashView<TextLineData, Nothing>
}

data class ViewId(val markers: List<Int>)

data class TextLineData(
    val text: String,
    val style: TextStyle
)

enum class TextStyle {
    Highlight5,
    Highlight6,
    Subtitle1,
}
