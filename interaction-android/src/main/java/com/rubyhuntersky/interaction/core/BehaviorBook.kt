package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BehaviorBook<T : Any>(start: T? = null) : Book<T> {

    private val subject: BehaviorSubject<T> =
        start?.let { BehaviorSubject.createDefault(start) } ?: BehaviorSubject.create()

    private val writer = subject.toSerialized()

    override val reader: Observable<T> get() = subject.distinctUntilChanged()
    override fun write(value: T) = writer.onNext(value)
}