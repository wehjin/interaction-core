package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Book<T : Any> {
    val value: T get() = reader.blockingFirst()
    val reader: Observable<T>
    fun write(value: T)
}