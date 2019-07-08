package com.rubyhuntersky.interaction.app.alt

import com.rubyhuntersky.interaction.core.*

class AltStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = "AltStory"
    }
}

sealed class Vision {
    object Idle : Vision()
    data class Viewing(val text: String) : Vision()
}

sealed class Action {
    data class Load(val text: String) : Action()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybeEnding: Any?): Boolean = false

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Load -> Revision(Vision.Viewing(action.text))
    else -> Revision(vision)
}
