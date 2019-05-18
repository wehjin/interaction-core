package com.rubyhuntersky.interaction.core

interface Well {
    fun <A : Any> addWishes(wishes: List<Wish<A>>, interaction: Interaction<*, A>)
}
