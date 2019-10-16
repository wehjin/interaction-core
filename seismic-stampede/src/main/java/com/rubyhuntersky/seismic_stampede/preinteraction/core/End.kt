package com.rubyhuntersky.seismic_stampede.preinteraction.core

sealed class End<out T : Any> {

    abstract val value: T?
    abstract fun <U : Any> map(transform: (T) -> U): End<U>

    data class High<out T : Any>(
        override val value: T
    ) : End<T>() {
        override fun <U : Any> map(transform: (T) -> U) = High(transform(value))
    }


    object Flat : End<Nothing>() {
        override val value = null
        override fun <U : Any> map(transform: (Nothing) -> U) = this
    }

    data class Low(
        val error: Throwable
    ) : End<Nothing>() {
        override val value = null
        override fun <U : Any> map(transform: (Nothing) -> U) = this
    }
}

fun <T : Any> endHigh(value: T) = End.High(value)
fun endFlat() = End.Flat
fun endLow(error: Throwable) = End.Low(error)
