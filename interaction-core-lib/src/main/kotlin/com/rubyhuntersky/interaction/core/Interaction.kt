package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Interaction<V, A> {

    val name: String
    val visionStream: Observable<V>
    fun sendAction(action: A)
    fun reset() {}
}
