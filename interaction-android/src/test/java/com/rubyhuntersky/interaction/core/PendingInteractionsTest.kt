package com.rubyhuntersky.interaction.core

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertEquals
import org.junit.Test

class PendingInteractionsTest {

    private val subject = BehaviorSubject.createDefault(1)

    private val interaction = object : Interaction<Int, Unit> {

        override var edge: Edge = Edge()
        override val visions: Observable<Int> get() = subject

        override fun isEnding(someVision: Any?): Boolean = (someVision as? Int)?.equals(2) ?: false
        override fun sendAction(action: Unit) = Unit
    }

    @Test
    fun followCallsEndFunctionOnceWhenInteractionEnds() {
        var calls = 0
        val pending = PendingInteractions()
        pending.follow(interaction) { calls++ }
        subject.onNext(7)
        subject.onNext(2)
        subject.onNext(2)
        assertEquals(1, calls)
    }

    @Test
    fun countDropsWhenInteractionEnds() {
        val pending = PendingInteractions()
        pending.follow(interaction) {}
        subject.onNext(2)
        assertEquals(0, pending.count)
    }

    @Test
    fun countRisesWithFollow() {
        val pending = PendingInteractions()
        pending.follow(interaction) {}
        assertEquals(1, pending.count)
    }
}