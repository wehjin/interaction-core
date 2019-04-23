package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Reporter<R, A> {
    val name: String
    val reportStream: Observable<R>
    fun sendAction(action: A)
}