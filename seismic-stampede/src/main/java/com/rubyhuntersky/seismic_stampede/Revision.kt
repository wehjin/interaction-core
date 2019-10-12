package com.rubyhuntersky.seismic_stampede

data class Revision<V : Any>(val newVision: V, val isLast: Boolean = false)