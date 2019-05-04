package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.Single

interface Interaction<V, A> {

    val name: String
        get() = this.javaClass.simpleName

    val visionStream: Observable<V>

    fun sendAction(action: A)

    fun isTailVision(someVision: Any?) = false

    val tailVision: Single<V>
        get() = visionStream.filter { isTailVision(it) }.firstOrError()
}
