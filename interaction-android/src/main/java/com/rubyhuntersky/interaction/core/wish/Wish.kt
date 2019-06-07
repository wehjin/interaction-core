package com.rubyhuntersky.interaction.core.wish

data class Wish<out Params : Any, out Action : Any>(
    val name: String,
    val params: Params,
    val kind: WishKind<Action>
) {

    companion object {
        fun <Action : Any> none(name: String): Wish<Unit, Action> {
            return Wish(name, Unit, WishKind.None())
        }
    }
}
