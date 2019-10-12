package com.rubyhuntersky.seismic_stampede.plots

import com.rubyhuntersky.seismic_stampede.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

object BuildSessionPlot {

    val storyName: String = this::class.java.simpleName

    sealed class Vision : Revisable {
        data class BuildPassword(val checker: (ByteArray) -> String?, val target: File) : Vision()
        data class Ended(val ending: Ending<Session>) : Vision()
    }

    sealed class Action {
        class SetPassword(val password: ByteArray, val target: File) : Action()
    }

    @ExperimentalCoroutinesApi
    fun start(folder: File, storybook: Storybook): Story2<Vision, Action> {
        val minLength = 3
        val init = Vision.BuildPassword(
            checker = { password ->
                if (password.size < minLength) "Password must be at least $minLength characters long."
                else null
            },
            target = folder
        )
        val story =
            storyOf<Vision, Action>(
                storyName,
                init
            ) { action, vision ->
                when (action) {
                    is Action.SetPassword -> {
                        require(vision is Vision.BuildPassword)
                        val vault =
                            vaultOf(
                                action.password,
                                action.target
                            )
                        val session = Session(
                            KeyStack.Shallow(action.password),
                            vault
                        )
                        Vision.Ended(
                            okEnding(
                                session
                            )
                        )
                            .toRevision(isLast = true)
                    }
                }
            }
        return story.also(storybook::project)
    }
}