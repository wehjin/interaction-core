package com.rubyhuntersky.interaction.core

data class Revision<V, A>(val newVision: V, val wishes: List<Wish<A>> = emptyList()) {
    constructor(newVision: V, wish: Wish<A>) : this(newVision, listOf(wish))
}