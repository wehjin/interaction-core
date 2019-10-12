package com.rubyhuntersky.seismic_stampede.preinteraction.core

sealed class Ending<out T : Any> {

    abstract val value: T?

    data class Ok<out T : Any>(override val value: T) : Ending<T>()

    object Cancel : Ending<Nothing>() {
        override val value = null

    }

    data class Error(val error: Throwable) : Ending<Nothing>() {
        override val value = null
    }
}

fun <T : Any> okEnding(value: T) = Ending.Ok(value)
fun cancelEnding() = Ending.Cancel
fun errorEnding(error: Throwable) = Ending.Error(error)
