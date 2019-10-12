package com.rubyhuntersky.seismic_stampede.preinteraction.core

data class Revision<V : Any>(val newVision: V, val isLast: Boolean = false)