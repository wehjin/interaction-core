package com.rubyhuntersky.interaction.interactions.common

import io.reactivex.Observable

interface Interaction<V, A> {
    fun reset() {}

    val visionStream: Observable<V>
    fun sendAction(action: A)
}

