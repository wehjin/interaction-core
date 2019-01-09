package com.rubyhuntersky.interaction.interactions.main

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.symbolsearch.ConstituentSearchCatalyst
import io.reactivex.Observable
import org.junit.Test

class MainInteractionTest {

    private val mockConstituentSearchCatalyst = mock<ConstituentSearchCatalyst> {}

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(rebellion: Rebellion) = Unit
        }

        MainInteraction(rebellionBook, mockConstituentSearchCatalyst).visionStream.test()
            .assertSubscribed()
            .assertValues(MainInteraction.Vision.Loading)
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.fromArray(Rebellion.SEED)
            override fun write(rebellion: Rebellion) = Unit
        }

        MainInteraction(rebellionBook, mockConstituentSearchCatalyst).visionStream.test()
            .assertSubscribed()
            .assertValue { it is MainInteraction.Vision.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun findConstituentActionStartsConstituentSearchInteraction() {
        val interaction = MainInteraction(MemoryRebellionBook(), mockConstituentSearchCatalyst)
        interaction.update(MainInteraction.Action.FindConstituent)
        verify(mockConstituentSearchCatalyst).catalyze()
    }
}