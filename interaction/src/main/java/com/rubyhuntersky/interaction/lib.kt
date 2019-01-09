package com.rubyhuntersky.interaction

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface Interaction<V, in A> {
    val visionStream: Observable<V>
    fun update(action: A)
}

interface InteractionCatalyst {
    fun catalyze()
}

object NotImplementedCatalyst : InteractionCatalyst {
    override fun catalyze() {
        check(false) { "Catalyst not implemented" }
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply { compositeDisposable.add(this) }
