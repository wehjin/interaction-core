package com.rubyhuntersky.interaction.app.select

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.Well

sealed class Vision {
    data class Options(val options: List<String>) : Vision()
    data class Choice(val choice: String?) : Vision()
}

sealed class Action {
    data class SetChoice(val choice: Int?) : Action()
}

class SelectOptionStory(well: Well, vararg options: String) : Interaction<Vision, Action>
by Story(
    well,
    start = { Vision.Options(options.toList()) },
    isEnding = { some -> some is Vision.Choice },
    revise = { oldVision, action ->
        if (oldVision is Vision.Options) {
            val setChoiceAction = action as Action.SetChoice
            val choice = setChoiceAction.choice?.let { oldVision.options[it] }
            Revision(Vision.Choice(choice))
        } else {
            Revision(oldVision)
        }
    },
    customGroup = TAG
) {

    companion object {
        val TAG: String = this::class.java.simpleName
    }
}

