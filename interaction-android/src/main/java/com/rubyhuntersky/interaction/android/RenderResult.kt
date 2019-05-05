package com.rubyhuntersky.interaction.android

sealed class RenderResult<Data> {
    abstract val data: Data

    data class Continue<Data>(override val data: Data) : RenderResult<Data>()
    data class Finish<Data>(override val data: Data) : RenderResult<Data>()
}