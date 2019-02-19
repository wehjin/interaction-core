package com.rubyhuntersky.interaction.correctiondetails

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Saver
import com.rubyhuntersky.interaction.core.BehaviorInteractionAdapter as Adapter

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
    updateSharesPortal: Portal<AssetSymbol>
) : CorrectionDetailsInteraction
by Saver.InteractionImpl(saverBook).adapt(CorrectionDetailsSaverAdapter(updateSharesPortal))

class CorrectionDetailsSaverAdapter(private val updateSharesPortal: Portal<AssetSymbol>) :
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
                    is Vision.Viewing -> updateSharesPortal.jump(vision.details.assetSymbol)
                }
            }
        }
}