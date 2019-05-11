package com.rubyhuntersky.interaction.app.select

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.Well

class SelectOptionStory(well: Well, vararg options: String) : Interaction<SelectVision, SelectAction>
by Story(
    well,
    start = { SelectVision.Options(options.toList()) },
    isEnding = { some -> some is SelectVision.Choice },
    revise = { oldVision, action ->
        if (oldVision is SelectVision.Options) {
            val setChoiceAction = action as SelectAction.SetChoice
            val choice = setChoiceAction.choice?.let { oldVision.options[it] }
            Revision(SelectVision.Choice(choice))
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

sealed class SelectVision {
    data class Options(val options: List<String>) : SelectVision()
    data class Choice(val choice: String?) : SelectVision()
}

sealed class SelectAction {
    data class SetChoice(val choice: Int?) : SelectAction()
}

