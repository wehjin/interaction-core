package com.rubyhuntersky.interaction.core

import com.rubyhuntersky.interaction.core.wish.Wish

interface Well {
    fun <A : Any> addWishes(wishes: List<Wish<*, A>>, interaction: Interaction<*, A>)
}
