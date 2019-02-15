package com.rubyhuntersky.vx

data class Input(
    val label: String,
    val text: String
)


object InputDash : Dash<Input, Nothing> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<Input, Nothing> = viewHost.addInput(id)
}
