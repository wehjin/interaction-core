package com.rubyhuntersky.interaction.app.select

import com.rubyhuntersky.interaction.app.alt.AltStory
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.app.alt.Action as AltAction

const val SELECT_OPTION = "SelectOption"

class SelectOptionStory(vararg options: String) :
    Interaction<Vision, Action> by Story({ start(options) }, ::isEnding, ::revise, SELECT_OPTION)

sealed class Vision {

    data class Options(val options: List<String>) : Vision()
    data class Choice(val choice: String?) : Vision()
}

fun start(options: Array<out String>) = Vision.Options(options.toList())
fun isEnding(maybe: Any?) = maybe is Vision.Choice

sealed class Action {

    object Start : Action()
    data class SetChoice(val choice: Int?) : Action()
    data class Ignore(val ignore: Any?) : Action()
}

fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Options && action is Action.Start -> {
        val newOptions = vision.options.toMutableList().also { it.add("S") }
        Revision(Vision.Options(newOptions))
    }
    vision is Vision.Options && action is Action.SetChoice -> {
        val choice = action.choice?.let { vision.options[it] }
        choice?.let {
            val altWish = edge.wish("alt", AltStory(), AltAction.Load(it), Action::Ignore)
            Revision(Vision.Choice(it), altWish)
        } ?: Revision(Vision.Choice(choice))
    }
    vision is Vision.Choice -> Revision(vision)
    action is Action.Ignore -> Revision(vision)
    else -> error("VISION: $vision, ACTION: $action")
}
