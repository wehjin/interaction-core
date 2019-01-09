package com.rubyhuntersky.interaction.interactions.main

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.interaction.InteractionCatalyst
import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.books.RebellionBook
import io.reactivex.Observable
import org.junit.Test

class MainInteractionTest {

    private val mockConstituentSearchCatalyst = mock<InteractionCatalyst> {}
    private val mockCashEditingCatalyst = mock<InteractionCatalyst> {}

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(rebellion: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(rebellionBook, mockConstituentSearchCatalyst, mockCashEditingCatalyst)

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValues(MainVision.Loading)
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.fromArray(Rebellion.SEED)
            override fun write(rebellion: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(rebellionBook, mockConstituentSearchCatalyst, mockCashEditingCatalyst)

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValue { it is MainVision.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun findConstituentActionStartsConstituentSearchInteraction() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            constituentSearchCatalyst = mockConstituentSearchCatalyst,
            cashEditingCatalyst = mockCashEditingCatalyst
        )

        mainInteraction.update(MainAction.FindConstituent)
        verify(mockConstituentSearchCatalyst).catalyze()
    }

    @Test
    fun openCashEditorActionCatalyzesCashEditingCatalyst() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            constituentSearchCatalyst = mockConstituentSearchCatalyst,
            cashEditingCatalyst = mockCashEditingCatalyst
        )
        mainInteraction.update(MainAction.OpenCashEditor)
        verify(mockCashEditingCatalyst).catalyze()
    }
}