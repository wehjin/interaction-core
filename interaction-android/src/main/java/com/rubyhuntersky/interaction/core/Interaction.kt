package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.Single

interface Interaction<V, A> {
    val group: String get() = this::class.java.simpleName
    val visions: Observable<V>
    val ending: Single<V> get() = visions.filter(this::isEnding).firstOrError()
    fun isEnding(someVision: Any?) = false
    fun sendAction(action: A)
}
