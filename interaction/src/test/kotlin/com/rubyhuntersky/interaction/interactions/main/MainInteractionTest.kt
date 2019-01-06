package com.rubyhuntersky.interaction.interactions.main

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.interaction.books.RebellionBook
import io.reactivex.Observable
import org.junit.Test

class MainInteractionTest {

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(rebellion: Rebellion) = Unit
        }

        MainInteraction(rebellionBook).state.test()
            .assertSubscribed()
            .assertValues(MainInteraction.State.Loading)
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.fromArray(Rebellion.SEED)
            override fun write(rebellion: Rebellion) = Unit
        }

        MainInteraction(rebellionBook).state.test()
            .assertSubscribed()
            .assertValue { it is MainInteraction.State.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }
}