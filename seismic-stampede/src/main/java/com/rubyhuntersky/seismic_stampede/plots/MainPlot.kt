package com.rubyhuntersky.seismic_stampede.plots

import com.rubyhuntersky.seismic_stampede.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

object MainPlot {

    sealed class Vision : Revisable {
        object Idle : Vision()
        data class Viewing(val session: Session) : Vision() {
            fun refresh() = copy(session = session.refresh())
        }

        data class Ended(val message: String?) : Vision()
    }

    sealed class Action {
        data class Load(val folder: File) : Action()
        data class SetSession(val session: Session) : Action()
        object Ignore : Action()
        data class Quit(val message: String? = null) : Action()
        object Refresh : Action()
    }

    @ExperimentalCoroutinesApi
    fun start(storybook: Storybook): Story2<Vision, Action> {
        return storyOf<Vision, Action>(
            name = "Main",
            init = Vision.Idle as Vision
        ) { action, vision, offer ->
            when (action) {
                is Action.Load -> {
                    val vault = vaultAt(action.folder)
                    if (vault == null) {
                        BuildSessionPlot.start(
                            action.folder,
                            storybook
                        ).monitor { subVision ->
                            if (subVision is BuildSessionPlot.Vision.Ended) {
                                when (val ending = subVision.ending) {
                                    is Ending.Ok -> offer(
                                        Action.SetSession(
                                            ending.value
                                        )
                                    )
                                    is Ending.Cancel -> offer(
                                        Action.Quit()
                                    )
                                    is Ending.Error -> offer(
                                        Action.Quit(
                                            ending.error.localizedMessage
                                        )
                                    )
                                }
                                RenderStatus.Stop
                            } else {
                                RenderStatus.Repeat
                            }
                        }
                        vision.toRevision()
                    } else {
                        val session = Session(
                            KeyStack.Empty,
                            vault,
                            0
                        )
                        Vision.Viewing(session)
                            .toRevision()
                    }
                }
                is Action.SetSession -> Vision.Viewing(
                    action.session
                ).toRevision()
                is Action.Ignore -> vision.toRevision()
                is Action.Refresh -> (vision as Vision.Viewing).refresh().toRevision()
                is Action.Quit -> Vision.Ended(
                    action.message
                ).toRevision(isLast = true)
            }
        }
            .apply { offer(Action.Load(defaultFolder)) }
    }
}