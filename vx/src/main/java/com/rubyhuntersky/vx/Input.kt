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

object InputDash : Dash<Input, Nothing> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<Input, Nothing> = viewHost.addInput(id)
}
