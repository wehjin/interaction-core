package com.rubyhuntersky.interaction.core

import com.rubyhuntersky.interaction.core.wish.Wish

data class Revision<V, A : Any>(val newVision: V, val wishes: List<Wish<*, A>> = emptyList()) {
    constructor(newVision: V, wish: Wish<*, A>) : this(newVision, listOf(wish))
    constructor(newVision: V, vararg wishes: Wish<*, A>) : this(newVision, wishes.toList())
}