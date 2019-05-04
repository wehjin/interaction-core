package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Interaction<V, A> {

    val name: String get() = this.javaClass.simpleName
    val visionStream: Observable<V>
    fun sendAction(action: A): Unit = throw UnsupportedOperationException()
    fun reset() = Unit
}
