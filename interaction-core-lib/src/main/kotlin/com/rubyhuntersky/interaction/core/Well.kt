package com.rubyhuntersky.interaction.core

interface Well {
    fun <A> addWishes(wishes: List<Wish<A>>, interaction: Interaction<*, A>)
}
