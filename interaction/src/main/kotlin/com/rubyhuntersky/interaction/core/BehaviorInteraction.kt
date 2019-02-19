package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

abstract class BehaviorInteraction<V, A>(startVision: V? = null, private val startAction: A? = null) :
    Interaction<V, A> {

    override val visionStream: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionBehavior: BehaviorSubject<V> =
        startVision?.let { BehaviorSubject.createDefault(startVision) } ?: BehaviorSubject.create()

    protected val vision get() = visionBehavior.value!!
    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    private val visionWriter = visionBehavior.toSerialized()

    override fun reset() {
        startAction?.let(this::sendAction)
    }


    fun <EdgeV, EdgeA> adapt(adapter: BehaviorInteractionAdapter<V, A, EdgeV, EdgeA>): Interaction<EdgeV, EdgeA> {
        val core = this
        return object : BehaviorInteraction<EdgeV, EdgeA>() {
            private val edge = this
            private val coreVisions = CompositeDisposable()
            private val controller = object :
                BehaviorInteractionAdapter.Controller<EdgeV, A> {
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
}

interface BehaviorInteractionAdapter<CoreV, CoreA, EdgeV, EdgeA> {

    fun onVision(vision: CoreV, controller: Controller<EdgeV, CoreA>)
    fun onAction(action: EdgeA, controller: Controller<EdgeV, CoreA>)

    interface Controller<EdgeV, CoreA> {
        val vision: EdgeV
        fun setVision(vision: EdgeV)
        fun sendUpstreamAction(action: CoreA)
    }
}
