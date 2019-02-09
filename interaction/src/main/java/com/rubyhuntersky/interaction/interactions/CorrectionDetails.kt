package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.interaction.interactions.common.Persist
import com.rubyhuntersky.interaction.BehaviorInteractionAdapter as Adapter
import com.rubyhuntersky.interaction.interactions.common.Interaction as CommonInteraction

object CorrectionDetails {

    sealed class Vision {
        data class Wrap(val unwrap: Persist.Vision<Correction>) : Vision()
    }

    sealed class Action {
        data class Wrap(val unwrap: Persist.Action<Correction>) : Action()
        object UpdateShares : Action()
    }

    class Interaction(correctionBook: Book<Correction>, updateSharesCatalyst: Catalyst<AssetSymbol>) :
        CommonInteraction<Vision, Action> by Persist.Interaction(correctionBook)
            .adapt(object : Adapter<Persist.Vision<Correction>, Persist.Action<Correction>, Vision, Action> {

                override fun onVision(
                    vision: Persist.Vision<Correction>,
                    controller: Adapter.Controller<Vision, Persist.Action<Correction>>
                ) = controller.setVision(Vision.Wrap(vision))

                override fun onAction(
                    action: Action,
                    controller: Adapter.Controller<Vision, Persist.Action<Correction>>
                ) =
                    when (action) {
                        is Action.Wrap -> controller.sendUpstreamAction(action.unwrap)
                        is Action.UpdateShares -> {
                            val vision = controller.vision
                            when (vision) {
                                is Vision.Wrap -> when (vision.unwrap) {
                                    is Persist.Vision.Reading -> Unit
                                    is Persist.Vision.Ready -> updateSharesCatalyst.catalyze(vision.unwrap.value.assetSymbol)
                                }
                            }
                        }
                    }
            })
}