package com.rubyhuntersky.interaction.app.select

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Well
import com.rubyhuntersky.interaction.core.WellInteraction
import com.rubyhuntersky.interaction.core.WellResult

class SelectInteraction(well: Well, vararg options: String) : Interaction<SelectVision, SelectAction>
by WellInteraction(
    well,
    start = { SelectVision.Options(options.toList()) },
    update = { oldVision, action ->
        if (oldVision is SelectVision.Options) {
            val setChoiceAction = action as SelectAction.SetChoice
            val choice = setChoiceAction.choice?.let { oldVision.options[it] }
            WellResult(SelectVision.Choice(choice))
        } else {
            WellResult(oldVision)
        }
    },
    isTail = { some -> some is SelectVision.Choice },
    customName = TAG
) {

    fun result(fill: () -> String) = tailVision.map {
        val value = (it as SelectVision.Choice).choice ?: fill()
        value
    }

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

