package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Story<V : Any, A : Any, R : Any> {
    val name: String
    val visions: Observable<V>
    val reports: Observable<R>
    fun update(action: A)
    fun end()
}