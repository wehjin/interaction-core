package com.rubyhuntersky.interaction.app.select

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story

const val SELECT_OPTION = "SelectOption"

class SelectOptionStory(vararg options: String) : Interaction<Vision, Action>
by Story({ start(options) }, ::isEnding, ::revise, SELECT_OPTION)

sealed class Vision {

    data class Options(val options: List<String>) : Vision()
    data class Choice(val choice: String?) : Vision()
}

fun start(options: Array<out String>) = Vision.Options(options.toList())
fun isEnding(maybe: Any?) = maybe is Vision.Choice

sealed class Action {

    object Start : Action()
    data class SetChoice(val choice: Int?) : Action()
}

fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        vision is Vision.Options && action is Action.Start -> {
            val newOptions = vision.options.toMutableList().also { it.add("S") }
            val newVision = Vision.Options(newOptions) as Vision
            Revision(newVision)
        }
        vision is Vision.Options && action is Action.SetChoice -> {
            val choice = action.choice?.let { vision.options[it] }
            Revision(Vision.Choice(choice))
        }
        vision is Vision.Choice -> {
            Revision(vision)
        }
        else -> throw NotImplementedError("VISION: $vision, ACTION: $action")
    }
}
