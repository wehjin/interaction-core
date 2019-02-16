package com.rubyhuntersky.vx

data class Input(
    val text: String,
    val originalText: String,
    val label: String,
    val icon: Icon?
)

sealed class Icon {
    data class ResId(val resId: Int) : Icon()
}

sealed class InputEvent {
    data class TextChange(val text: String) : InputEvent()
}

object InputDash : Dash<Input, InputEvent> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<Input, InputEvent> = viewHost.addInput(id)
}
