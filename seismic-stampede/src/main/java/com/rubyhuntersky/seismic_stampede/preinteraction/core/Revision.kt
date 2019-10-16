package com.rubyhuntersky.seismic_stampede.preinteraction.core

import com.rubyhuntersky.seismic_stampede.vibes.Wish2

data class Revision<V : Any, A : Any>(
    val newVision: V,
    val isLast: Boolean = false,
    val wishes: List<Wish2<A>> = emptyList()
)