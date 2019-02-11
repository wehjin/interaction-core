package com.rubyhuntersky.vx

data class TextLine(
    val text: String,
    val style: TextStyle
)

enum class TextStyle {
    Highlight5,
    Highlight6,
    Subtitle1,
}

class TextLineDash : Dash<TextLine, Nothing> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<TextLine, Nothing> = viewHost.addTextLine(id)
}
