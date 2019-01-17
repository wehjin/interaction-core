package com.rubyhuntersky.interaction.interactions.common

import io.reactivex.Observable

interface Interaction<V, in A> {
    fun reset() {}

    val visionStream: Observable<V>
    fun onAction(action: A)
}