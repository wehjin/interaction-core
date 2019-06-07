package com.rubyhuntersky.interaction.app.main

import android.util.Log
import com.rubyhuntersky.interaction.app.select.SelectOptionStory
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.interval.Interval
import com.rubyhuntersky.interaction.core.wish.interval.IntervalDjinn
import java.util.concurrent.TimeUnit
import com.rubyhuntersky.interaction.app.select.Action as SelectOptionAction
import com.rubyhuntersky.interaction.app.select.Vision as SelectionVision

sealed class Vision {
    data class Message(val message: String) : Vision()
}

private fun start() = Vision.Message("Idle")

private fun isEnding(maybe: Any?) = maybe is Nothing

sealed class Action {
    object Select : Action()
    data class ReceiveSelection(val selection: String) : Action()
    data class SetMessage(val message: String) : Action()
    object Cancel : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    Log.d(MainStory.TAG, "ACTION: $action")
    return when (action) {
        is Action.Select -> {
            val wish = edge.wish(
                name = "selection",
                interaction = SelectOptionStory("A", "B", "C"),
                startAction = SelectOptionAction.Start
            ) {
                val choice = it as SelectionVision.Choice
                Action.ReceiveSelection(choice.choice ?: "Cancelled") as Action
            }
            Revision(vision, wish)
        }
        is Action.ReceiveSelection -> {
            val newVision = Vision.Message(action.selection)
            val wish = IntervalDjinn.newWish(
                name = "interval",
                interval = Interval(3, TimeUnit.SECONDS),
                indexToAction = { index -> Action.SetMessage("${action.selection}: $index") as Action }
            )
            Revision(newVision, wish)
        }
        is Action.SetMessage -> {
            Revision(Vision.Message(action.message))
        }
        is Action.Cancel -> {
            Revision(Vision.Message("Cancelled"), Wish.none("selection"), Wish.none("interval"))
        }
    }
}

class MainStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, TAG) {

    companion object {
        val TAG: String = MainStory::javaClass.name
    }
}
