package com.rubyhuntersky.interaction.interactions

import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.BehaviorInteractionAdapter
import com.rubyhuntersky.interaction.books.Book
import com.rubyhuntersky.interaction.interactions.common.ReadWrite
import com.rubyhuntersky.interaction.interactions.common.Interaction as CommonInteraction

object CorrectionDetails {

    sealed class Vision {
        data class Wrap(val unwrap: ReadWrite.Vision<Correction>) : Vision()
    }

    sealed class Action {
        data class Wrap(val unwrap: ReadWrite.Action<Correction>) : Action()
    }

    class Interaction(book: Book<Correction>) : CommonInteraction<Vision, Action>
    by ReadWrite.Interaction(book).adapt(object :
        BehaviorInteractionAdapter<ReadWrite.Vision<Correction>, ReadWrite.Action<Correction>, Vision, Action> {

        override fun onUpstreamVision(
            upstreamVision: ReadWrite.Vision<Correction>,
            setVision: (vision: Vision) -> Unit,
            sendUpstreamAction: (action: ReadWrite.Action<Correction>) -> Unit
        ) {
            setVision(Vision.Wrap(upstreamVision))
        }

        override fun onAction(
            action: Action,
            setVision: (vision: Vision) -> Unit,
            sendUpstreamAction: (action: ReadWrite.Action<Correction>) -> Unit
        ) {
            val upstreamAction = (action as Action.Wrap).unwrap
            sendUpstreamAction(upstreamAction)
        }
    })
}