package com.rubyhuntersky.interaction.app.main

import android.util.Log
import com.rubyhuntersky.interaction.app.MyApplication
import com.rubyhuntersky.interaction.app.select.SelectVision
import com.rubyhuntersky.interaction.core.*

sealed class Vision {
    data class Message(val message: String) : Vision()
}

private fun start() = Vision.Message("Idle")

private fun isEnding(maybe: Any?) = false

sealed class Action {
    object Select : Action()
    data class SetMessage(val message: String) : Action()
}

private fun revise(vision: Vision, action: Action): Revision<Vision, Action> {

    Log.d(MainStory.TAG, "ACTION: $action")
    return when (action) {
        is Action.Select -> {
            val selectOption = MyApplication.selectOption("A", "B", "C")
            val wish = Wish(
                action = selectOption.ending.map {
                    val choice = it as SelectVision.Choice
                    Action.SetMessage(choice.choice ?: "Cancelled") as Action
                },
                name = "$MainStory/${selectOption.group}"
            )
            Revision(vision, wish)
        }
        is Action.SetMessage -> {
            Revision(Vision.Message(action.message))
        }
    }
}

class MainStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, TAG) {

    companion object {
        val TAG: String = MainStory::javaClass.name
    }
}
