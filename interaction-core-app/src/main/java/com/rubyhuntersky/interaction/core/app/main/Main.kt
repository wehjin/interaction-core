package com.rubyhuntersky.interaction.core.app.main

import android.util.Log
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.app.select.SelectInteraction

sealed class MainVision {
    data class Message(val message: String) : MainVision()
}

sealed class MainAction {
    object Select : MainAction()
    data class SetMessage(val message: String) : MainAction()
}

class MainInteraction(well: Well, private val edge: Edge) : Interaction<MainVision, MainAction>
by WellInteraction(
    well,
    start = { MainVision.Message("Idle") },
    update = { oldVision, action ->
        Log.d(TAG, "ACTION: $action")
        when (action) {
            is MainAction.Select -> {
                val interaction = SelectInteraction(well, "A", "B", "C")
                edge.presentInteraction(interaction)
                val wishAction = interaction.result { "Cancelled" }
                    .map {
                        MainAction.SetMessage(it) as MainAction
                    }
                WellResult(oldVision, Wish(wishAction, name = "${this}/${interaction.name}"))
            }
            is MainAction.SetMessage -> {
                WellResult(MainVision.Message(action.message))
            }
        }
    },
    isTail = { false },
    customName = TAG
) {
    companion object {
        val TAG = this::class.java.simpleName
    }
}