package com.rubyhuntersky.seismic_stampede.preinteraction.core

sealed class End<out T : Any> {

    abstract val value: T?

    data class High<out T : Any>(
        override val value: T
    ) : End<T>()

    object Flat : End<Nothing>() {
        override val value = null
    }

    data class Low(
        val error: Throwable
    ) : End<Nothing>() {
        override val value = null
    }
}

fun <T : Any> endHigh(value: T) = End.High(value)
fun endFlat() = End.Flat
fun endLow(error: Throwable) = End.Low(error)
