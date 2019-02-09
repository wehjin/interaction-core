package com.rubyhuntersky.interaction.interactions

import com.nhaarman.mockitokotlin2.mock
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.interactions.common.Persist
import org.junit.Test

class CorrectionDetailsTest {

    private val correction = Correction.Hold(AssetSymbol("TSL"), 1.0)
    private val mockUpdateSharesCatalyst = mock<Catalyst<AssetSymbol>>()

    private val interaction = CorrectionDetails.Interaction(
        BehaviorBook(correction),
        mockUpdateSharesCatalyst
    )

    @Test
    fun construction() {
        interaction.visionStream.test()
            .assertValue(CorrectionDetails.Vision.Wrap(Persist.Vision.Ready(correction)))
    }

    @Test
    fun writeAction() {
        val newCorrection = Correction.Hold(AssetSymbol("TSL"), 0.5)
        interaction.sendAction(CorrectionDetails.Action.Wrap(Persist.Action.Write(newCorrection)))
        interaction.visionStream.test()
            .assertValue(CorrectionDetails.Vision.Wrap(Persist.Vision.Ready(newCorrection)))
    }
}