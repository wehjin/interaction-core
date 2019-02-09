package com.rubyhuntersky.interaction.interactions.correctiondetails

import com.nhaarman.mockitokotlin2.mock
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.interactions.common.Saver
import org.junit.Test

class CorrectionDetailsTest {

    private val correction = Correction.Hold(AssetSymbol("TSL"), 1.0)
    private val mockUpdateSharesCatalyst = mock<Catalyst<AssetSymbol>>()

    private val interaction = CorrectionDetails(
        BehaviorBook(correction),
        mockUpdateSharesCatalyst
    )

    @Test
    fun construction() {
        interaction.visionStream.test()
            .assertValue(Vision.Wrap(Saver.Vision.Ready(correction)))
    }

    @Test
    fun writeAction() {
        val newCorrection = Correction.Hold(AssetSymbol("TSL"), 0.5)
        interaction.sendAction(Action.Wrap(Saver.Action.Write(newCorrection)))
        interaction.visionStream.test()
            .assertValue(Vision.Wrap(Saver.Vision.Ready(newCorrection)))
    }
}