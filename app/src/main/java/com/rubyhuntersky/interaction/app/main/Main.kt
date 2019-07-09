package com.rubyhuntersky.interaction.app.main

import android.util.Log
import com.rubyhuntersky.interaction.app.main.MainStory.Companion.groupId
import com.rubyhuntersky.interaction.app.select.SelectOptionStory
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.interval.Intervals
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
    Log.d(groupId, "ACTION: $action")
    return when (action) {
        is Action.Select -> {
            val wish = edge.wish(
                name = "selection",
                interaction = SelectOptionStory("A", "B", "C"),
                startAction = SelectOptionAction.Start
            ) {
                val choice = it as SelectionVision.Choice
                Action.ReceiveSelection(choice.choice ?: "Cancelled")
            }
            Revision(vision, wish)
        }
        is Action.ReceiveSelection -> {
            val newVision = Vision.Message(action.selection)
            val intervals = Intervals(3, TimeUnit.SECONDS)
                .toWish<Intervals, Action>(
                    "interval",
                    onResult = { index -> Action.SetMessage("${action.selection}: $index") },
                    onError = { throw it }
                )
            Revision(newVision, intervals)
        }
        is Action.SetMessage -> {
            Revision(Vision.Message(action.message))
        }
        is Action.Cancel -> {
            Revision(Vision.Message("Cancelled"), Wish.cancel("selection"), Wish.none("interval"))
        }
    }
}

class MainStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = MainStory::javaClass.name
    }
}
