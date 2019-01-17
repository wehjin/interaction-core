package com.rubyhuntersky.interaction

import com.rubyhuntersky.interaction.interactions.common.Interaction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

interface InteractionCatalyst {
    fun catalyze()
}

object NotImplementedCatalyst : InteractionCatalyst {
    override fun catalyze() {
        check(false) { "Catalyst not implemented" }
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply { compositeDisposable.add(this) }

abstract class BasicInteraction<V, A>(startingVision: V, private val resetAction: A? = null) :
    Interaction<V, A> {

    override val visionStream: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionBehavior = BehaviorSubject.createDefault(startingVision)

    protected val vision get() = visionBehavior.value!!
    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    private val visionWriter = visionBehavior.toSerialized()

    override fun reset() {
        resetAction?.let(this::update)
    }
}
