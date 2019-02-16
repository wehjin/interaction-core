package com.rubyhuntersky.interaction.interactions.main

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.main.Action
import com.rubyhuntersky.interaction.main.MainInteraction
import com.rubyhuntersky.interaction.main.Vision
import io.reactivex.Observable
import org.junit.Test

class MainInteractionTest {

    private val mockCorrectionDetailsCatalyst = mock<Catalyst<CorrectionDetails>> {}
    private val mockConstituentSearchCatalyst = mock<Catalyst<Unit>> {}
    private val mockCashEditingCatalyst = mock<Catalyst<Unit>> {}

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(
            rebellionBook,
            mockCorrectionDetailsCatalyst,
            mockConstituentSearchCatalyst,
            mockCashEditingCatalyst
        )

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValues(Vision.Loading)
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.fromArray(Rebellion.SEED)
            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(
            rebellionBook,
            mockCorrectionDetailsCatalyst,
            mockConstituentSearchCatalyst,
            mockCashEditingCatalyst
        )

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValue { it is Vision.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun findConstituentActionStartsConstituentSearchInteraction() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            correctionDetailCatalyst = mockCorrectionDetailsCatalyst,
            constituentSearchCatalyst = mockConstituentSearchCatalyst,
            cashEditingCatalyst = mockCashEditingCatalyst
        )

        mainInteraction.sendAction(Action.FindConstituent)
        verify(mockConstituentSearchCatalyst).catalyze(Unit)
    }

    @Test
    fun openCashEditorActionCatalyzesCashEditingCatalyst() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            correctionDetailCatalyst = mockCorrectionDetailsCatalyst,
            constituentSearchCatalyst = mockConstituentSearchCatalyst,
            cashEditingCatalyst = mockCashEditingCatalyst
        )
        mainInteraction.sendAction(Action.OpenCashEditor)
        verify(mockCashEditingCatalyst).catalyze(Unit)
    }
}