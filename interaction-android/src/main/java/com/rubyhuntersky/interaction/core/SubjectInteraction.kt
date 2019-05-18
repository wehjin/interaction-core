package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class SubjectInteraction<V, A>(
    startVision: V? = null,
    @Deprecated("Use sendAction") private val startAction: A? = null
) : Interaction<V, A> {

    override val group: String get() = this.javaClass.simpleName
    override lateinit var edge: Edge

    private val visionBehavior: BehaviorSubject<V> =
        startVision?.let { BehaviorSubject.createDefault(startVision) } ?: BehaviorSubject.create()
    protected val vision get() = visionBehavior.value!!
    override val visions: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionWriter = visionBehavior.toSerialized()

    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    protected fun endVisions() = visionWriter.onComplete()

    @Deprecated("Use sendAction")
    fun reset() {
        startAction?.let { sendAction(it) }
    }
}
