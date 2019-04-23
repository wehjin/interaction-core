package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

abstract class BehaviorInteraction<V, A>(
    startVision: V? = null, startAction: A? = null
) : SubjectQuantum<V, A, Unit>(startVision, startAction) {

    fun <EdgeV, EdgeA> adapt(adapter: BehaviorInteractionAdapter<V, A, EdgeV, EdgeA>): Interaction<EdgeV, EdgeA> {
        val core = this
        return object : BehaviorInteraction<EdgeV, EdgeA>() {
            override val name: String = "Adapted${core.name}"
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
