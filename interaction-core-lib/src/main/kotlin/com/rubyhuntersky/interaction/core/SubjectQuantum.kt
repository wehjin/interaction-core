package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class SubjectQuantum<V, A, R>(
    startVision: V? = null,
    private val startAction: A? = null
) : Quantum<V, A, R> {

    override val name: String get() = this.javaClass.simpleName

    private val visionBehavior: BehaviorSubject<V> = startVision?.let {
        BehaviorSubject.createDefault(startVision)
    } ?: BehaviorSubject.create()
    protected val vision get() = visionBehavior.value!!
    override val visionStream: Observable<V> get() = visionBehavior.distinctUntilChanged()
    private val visionWriter = visionBehavior.toSerialized()
    protected fun setVision(nextVision: V) = visionWriter.onNext(nextVision)
    override fun reset() {
        startAction?.let(this::sendAction)
    }

    private val reportPublish: PublishSubject<R> = PublishSubject.create()
    override val reportStream: Observable<R> = reportPublish
    private val reportWriter = reportPublish.toSerialized()
    protected fun sendReport(report: R) = reportWriter.onNext(report)
}
