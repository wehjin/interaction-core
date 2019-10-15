package com.rubyhuntersky.seismic_stampede.plots

import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

object PasswordPlot {

    val storyName: String = this::class.java.simpleName

    sealed class Vision : Revisable {
        data class BuildPassword(val checker: (CharArray) -> String?) : Vision()
        data class Ended(val end: End<Int>) : Vision()
    }

    sealed class Action {
        class SetPassword(val passwordId: Int) : Action()
        object Cancel : Action()
    }

    @ExperimentalCoroutinesApi
    fun start(storybook: Storybook): Story2<Vision, Action> {
        val minLength = 5
        val init = Vision.BuildPassword(
            checker = { password ->
                if (password.size < minLength) "Password must be at least $minLength characters long."
                else null
            }
        )
        val story = storyOf<Vision, Action>(storyName, init) { action, vision ->
            try {
                when (action) {
                    is Action.SetPassword -> {
                        require(vision is Vision.BuildPassword)
                        Vision.Ended(endHigh(action.passwordId)).toRevision(isLast = true)
                    }
                    is Action.Cancel -> {
                        Vision.Ended(endFlat()).toRevision(isLast = true)
                    }
                }
            } catch (t: Throwable) {
                Vision.Ended(endLow(t)).toRevision(isLast = true)
            }
        }
        return story.also(storybook::startProjector)
    }
}