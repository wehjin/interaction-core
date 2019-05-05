package com.rubyhuntersky.interaction.core

import io.reactivex.Observable

interface Story<V : Any, A : Any, R : Any> {
    val name: String
        get() = this.javaClass.simpleName

    val visions: Observable<V>
    fun update(action: A): Unit = throw UnsupportedOperationException()

    val reports: Observable<R>
        get() = Observable.never()

    fun end() {}
}