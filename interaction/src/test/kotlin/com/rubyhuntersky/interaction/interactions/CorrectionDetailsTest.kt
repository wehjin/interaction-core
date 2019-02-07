package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.interactions.common.ReadWrite
import org.junit.Test

class CorrectionDetailsTest {

    private val correction = Correction.Hold(AssetSymbol("TSL"), 1.0)
    private val interaction = CorrectionDetails.Interaction(BehaviorBook(correction))

    @Test
    fun construction() {
        interaction.visionStream.test()
            .assertValue(CorrectionDetails.Vision.Wrap(ReadWrite.Vision.Ready(correction)))
    }

    @Test
    fun writeAction() {
        val newCorrection = Correction.Hold(AssetSymbol("TSL"), 0.5)
        interaction.onAction(CorrectionDetails.Action.Wrap(ReadWrite.Action.Write(newCorrection)))
        interaction.visionStream.test()
            .assertValue(CorrectionDetails.Vision.Wrap(ReadWrite.Vision.Ready(newCorrection)))
    }
}