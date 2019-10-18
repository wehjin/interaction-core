package com.rubyhuntersky.seismic_stampede.preinteraction.core

data class Revision<V : Any, A : Any>(
    val newVision: V,
    val isLast: Boolean = false,
    val wishes: List<Wish2<A>> = emptyList()
)