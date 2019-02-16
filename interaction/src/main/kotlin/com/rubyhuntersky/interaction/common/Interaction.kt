package com.rubyhuntersky.interaction.common

import io.reactivex.Observable

typealias CommonInteraction<V, A> = Interaction<V, A>

interface Interaction<V, A> {
    fun reset() {}

    val visionStream: Observable<V>
    fun sendAction(action: A)
}
