package com.rubyhuntersky.seismic_stampede.preinteraction.core

interface Projector<V : Any, A : Any> {
    fun render(vision: V, offer: (A) -> Boolean): RenderStatus
}