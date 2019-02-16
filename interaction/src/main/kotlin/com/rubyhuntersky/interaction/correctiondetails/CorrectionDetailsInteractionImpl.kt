package com.rubyhuntersky.interaction.correctiondetails

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.interaction.common.Interaction
import com.rubyhuntersky.interaction.common.Saver
import com.rubyhuntersky.interaction.BehaviorInteractionAdapter as Adapter

typealias CorrectionDetailsInteraction = Interaction<Vision, Action>

sealed class Vision {
    object Loading : Vision()
    data class Viewing(val details: CorrectionDetails) : Vision()
}

sealed class Action {
    object UpdateShares : Action()
}

class CorrectionDetailsInteractionImpl(
    saverBook: Book<CorrectionDetails>,
    updateSharesCatalyst: Catalyst<AssetSymbol>
) : CorrectionDetailsInteraction
by Saver.InteractionImpl(saverBook).adapt(CorrectionDetailsSaverAdapter(updateSharesCatalyst))

class CorrectionDetailsSaverAdapter(private val updateSharesCatalyst: Catalyst<AssetSymbol>) :
    Adapter<Saver.Vision<CorrectionDetails>, Saver.Action<CorrectionDetails>, Vision, Action> {

    override fun onVision(
        vision: Saver.Vision<CorrectionDetails>,
        controller: Adapter.Controller<Vision, Saver.Action<CorrectionDetails>>
    ) = when (vision) {
        is Saver.Vision.Reading -> controller.setVision(Vision.Loading)
        is Saver.Vision.Ready -> controller.setVision(
            Vision.Viewing(
                vision.value
            )
        )
    }

    override fun onAction(action: Action, controller: Adapter.Controller<Vision, Saver.Action<CorrectionDetails>>) =
        when (action) {
            is Action.UpdateShares -> {
                val vision = controller.vision
                when (vision) {
                    is Vision.Loading -> Unit
                    is Vision.Viewing -> updateSharesCatalyst.catalyze(vision.details.assetSymbol)
                }
            }
        }
}