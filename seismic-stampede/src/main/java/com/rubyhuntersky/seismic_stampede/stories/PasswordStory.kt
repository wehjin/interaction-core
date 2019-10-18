package com.rubyhuntersky.seismic_stampede.stories

import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import com.rubyhuntersky.seismic_stampede.stories.PasswordStory.Action
import com.rubyhuntersky.seismic_stampede.stories.PasswordStory.Vision
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
fun startPasswordStory(storybook: Storybook): Story2<Vision, Action> = storyOf(
    family = PasswordStory.storyName,
    init = {
        Vision.BuildPassword(
            checker = { password ->
                val minLength = 5
                if (password.size < minLength) "Password must be at least $minLength characters long."
                else null
            }
        ).toRevision()
    },
    update = { action: Action, vision: Vision ->
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
).also(storybook::startProjector)

object PasswordStory {

    val storyName: String = this::class.java.simpleName

    sealed class Vision : Revisable<Vision, Action> {
        data class BuildPassword(val checker: (CharArray) -> String?) : Vision()
        data class Ended(val end: End<Int>) : Vision()
    }

    sealed class Action {
        class SetPassword(val passwordId: Int) : Action()
        object Cancel : Action()
    }
}