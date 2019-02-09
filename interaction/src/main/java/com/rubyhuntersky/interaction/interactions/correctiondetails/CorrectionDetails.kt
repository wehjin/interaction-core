package com.rubyhuntersky.interaction.interactions.correctiondetails

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.interaction.interactions.common.Interaction
import com.rubyhuntersky.interaction.interactions.common.Saver
import com.rubyhuntersky.interaction.BehaviorInteractionAdapter as Adapter

typealias CorrectionDetailsInteraction = Interaction<Vision, Action>

sealed class Vision {
    data class Wrap(val unwrap: Saver.Vision<Correction>) : Vision()
}

sealed class Action {
    data class Wrap(val unwrap: Saver.Action<Correction>) : Action()
    object UpdateShares : Action()
}

class CorrectionDetails(
    correctionBook: Book<Correction>,
    updateSharesCatalyst: Catalyst<AssetSymbol>
) : CorrectionDetailsInteraction
by Saver.InteractionImpl(correctionBook).adapt(CorrectionDetailsAdapter(updateSharesCatalyst))

class CorrectionDetailsAdapter(
    private val updateSharesCatalyst: Catalyst<AssetSymbol>
) : Adapter<Saver.Vision<Correction>, Saver.Action<Correction>, Vision, Action> {

    override fun onVision(
        vision: Saver.Vision<Correction>,
        controller: Adapter.Controller<Vision, Saver.Action<Correction>>
    ) = controller.setVision(Vision.Wrap(vision))

    override fun onAction(action: Action, controller: Adapter.Controller<Vision, Saver.Action<Correction>>) =
        when (action) {
            is Action.Wrap -> controller.sendUpstreamAction(action.unwrap)
            is Action.UpdateShares -> {
                val vision = controller.vision
                when (vision) {
                    is Vision.Wrap -> when (vision.unwrap) {
                        is Saver.Vision.Reading -> Unit
                        is Saver.Vision.Ready -> updateSharesCatalyst.catalyze(vision.unwrap.value.assetSymbol)
                    }
                }
            }
        }
}