package com.rubyhuntersky.interaction.app.main

import android.util.Log
import com.rubyhuntersky.interaction.app.MyApplication
import com.rubyhuntersky.interaction.core.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
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
            val interaction = MyApplication.selectionInteraction("A", "B", "C")
            val wish = edge.wish("selection", interaction) {
                val choice = it as SelectionVision.Choice
                Action.ReceiveSelection(choice.choice ?: "Cancelled") as Action
            }
            Revision(vision, wish)
        }
        is Action.ReceiveSelection -> {
            val newVision = Vision.Message(action.selection)
            val intervalWish = Observable.interval(3, TimeUnit.SECONDS)
                .toWish("interval", { interval ->
                    Action.SetMessage("${action.selection}: $interval") as Action
                }, { throw it })
            Revision(newVision, intervalWish)
        }
        is Action.SetMessage -> {
            Revision(Vision.Message(action.message))
        }
        is Action.Cancel -> {
            Revision(Vision.Message("Cancelled"), Wish.None("selection"), Wish.None("interval"))
        }
    }
}

class MainStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, TAG) {

    companion object {
        val TAG: String = MainStory::javaClass.name
    }
}
