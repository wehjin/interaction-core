package com.rubyhuntersky.interaction

import com.rubyhuntersky.interaction.interactions.common.Interaction
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

interface InteractionCatalyst<T> {
    fun catalyze(seed: T)
}

class NotImplementedCatalyst<T> : InteractionCatalyst<T> {
    override fun catalyze(seed: T) {
        check(false) { "Catalyst not implemented" }
    }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply { compositeDisposable.add(this) }

abstract class BehaviorInteraction<V, A>(startingVision: V? = null, private val resetAction: A? = null) :
    Interaction<V, A> {

    override val visionStream: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionBehavior: BehaviorSubject<V> =
        startingVision?.let { BehaviorSubject.createDefault(startingVision) } ?: BehaviorSubject.create()

    protected val vision get() = visionBehavior.value!!
    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    private val visionWriter = visionBehavior.toSerialized()

    override fun reset() {
        resetAction?.let(this::sendAction)
    }


    fun <EdgeV, EdgeA> adapt(adapter: BehaviorInteractionAdapter<V, A, EdgeV, EdgeA>): Interaction<EdgeV, EdgeA> {
        val core = this
        return object : BehaviorInteraction<EdgeV, EdgeA>() {

            private val coreVisions = CompositeDisposable()

            init {
                core.visionStream
                    .subscribe { coreVision ->
                        adapter.onUpstreamVision(coreVision, this::setVision, core::sendAction)
                    }
                    .addTo(coreVisions)
            }

            override fun sendAction(action: EdgeA) = adapter.onAction(action, this::setVision, core::sendAction)
        }
    }
}

interface BehaviorInteractionAdapter<CoreV, CoreA, EdgeV, EdgeA> {
    fun onUpstreamVision(
        upstreamVision: CoreV,
        setVision: (vision: EdgeV) -> Unit,
        sendUpstreamAction: (action: CoreA) -> Unit
    )

    fun onAction(action: EdgeA, setVision: (vision: EdgeV) -> Unit, sendUpstreamAction: (action: CoreA) -> Unit)
}
