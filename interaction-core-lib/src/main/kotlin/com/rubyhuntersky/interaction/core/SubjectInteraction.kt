package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

abstract class SubjectInteraction<V, A>(
    startVision: V? = null,
    @Deprecated("Use sendAction") private val startAction: A? = null
) : Interaction<V, A> {

    override val name: String get() = this.javaClass.simpleName

    private val visionBehavior: BehaviorSubject<V> =
        startVision?.let { BehaviorSubject.createDefault(startVision) } ?: BehaviorSubject.create()
    protected val vision get() = visionBehavior.value!!
    override val visionStream: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionWriter = visionBehavior.toSerialized()

    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    protected fun endVisions() = visionWriter.onComplete()

    @Deprecated("Use sendAction")
    fun reset() {
        startAction?.let { sendAction(it) }
    }
}

fun <V, A, EdgeV, EdgeA> SubjectInteraction<V, A>.adapt(adapter: SubjectInteractionAdapter<V, A, EdgeV, EdgeA>):
        Interaction<EdgeV, EdgeA> {

    val core = this

    return object : SubjectInteraction<EdgeV, EdgeA>() {
        override val name: String = "Adapted${core.name}"
        private val edge = this
        private val coreVisions = CompositeDisposable()

        private val controller =
            object : SubjectInteractionAdapter.Controller<EdgeV, A> {
                override val vision: EdgeV get() = edge.vision
                override fun setVision(vision: EdgeV) = edge.setVision(vision)
                override fun sendUpstreamAction(action: A) = core.sendAction(action)
            }

        init {
            core.visionStream
                .subscribe { adapter.onVision(it, controller) }
                .addTo(coreVisions)
        }

        override fun sendAction(action: EdgeA) = adapter.onAction(action, controller)
    }
}
