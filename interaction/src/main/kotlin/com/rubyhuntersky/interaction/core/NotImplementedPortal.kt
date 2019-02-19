package com.rubyhuntersky.interaction.core

class NotImplementedPortal<T> : Portal<T> {
    override fun jump(carry: T) = check(false) { "Not implemented" }
}