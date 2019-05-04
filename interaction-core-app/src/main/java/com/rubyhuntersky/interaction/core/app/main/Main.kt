package com.rubyhuntersky.interaction.core.app.main

import android.util.Log
import com.rubyhuntersky.interaction.core.*

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
        if (action is MainAction.Select) {
            Log.d(tag, "SELECT")
            val interaction = SelectInteraction(well, "A", "B", "C")
            edge.presentInteraction(interaction)
            val response = interaction.result { "Cancelled" }.map { MainAction.SetMessage(it) as MainAction }
            WellResult(oldVision, Wish(response, name = "${this}/${interaction.name}"))
        } else {
            WellResult(oldVision)
        }
    },
    customName = tag
) {
    companion object {
        private val tag = this::class.java.simpleName
        fun locateInEdge(edge: Edge): MainInteraction {
            val search = InteractionSearch.ByName(tag)
            return edge.findInteraction<MainVision, MainAction, Void>(search) as MainInteraction
        }
    }
}