package com.rubyhuntersky.seismic_stampede

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    Main.story.tellBlocking(::renderMain)
}

object Main {

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
    val story: Story2<Vision, Action> by lazy {
        storyOf<Vision, Action>(
            name = "Main",
            init = Vision.Idle as Vision
        ) { action, vision, offer ->
            when (action) {
                is Action.Load -> {
                    val vault = vaultAt(action.folder)
                    if (vault == null) {
                        BuildSession.start(action.folder).monitor { subVision ->
                            if (subVision is BuildSession.Vision.Ended) {
                                when (val ending = subVision.ending) {
                                    is Ending.Ok -> offer(Action.SetSession(ending.value))
                                    is Ending.Cancel -> offer(Action.Quit())
                                    is Ending.Error -> offer(Action.Quit(ending.error.localizedMessage))
                                }
                                RenderStatus.Stop
                            } else {
                                RenderStatus.Repeat
                            }
                        }
                        vision.toRevision()
                    } else {
                        val session = Session(KeyStack.Empty, vault, 0)
                        Vision.Viewing(session).toRevision()
                    }
                }
                is Action.SetSession -> Vision.Viewing(action.session).toRevision()
                is Action.Ignore -> vision.toRevision()
                is Action.Refresh -> (vision as Vision.Viewing).refresh().toRevision()
                is Action.Quit -> Vision.Ended(action.message).toRevision(isLast = true)
            }
        }.apply { offer(Action.Load(defaultFolder)) }
    }
}


private fun renderMain(
    vision: Main.Vision,
    offerAction: (action: Main.Action) -> Boolean
): RenderStatus {
    return when (vision) {
        is Main.Vision.Ended -> {
            vision.message?.let { Display.showLine(it) }
            RenderStatus.Stop
        }
        is Main.Vision.Viewing -> {
            val vault = vision.session.vault
            Display.showLine()
            Display.showMap(mapOf("Depth" to vault.depth.name))
            Display.showList("Gems", vault.activeGems.map { it.toString() })
            Display.showLine()
            offerAction(getAction())
            RenderStatus.Repeat
        }
        is Main.Vision.Idle -> RenderStatus.Repeat
    }
}

private fun getAction(): Main.Action {
    var action: Main.Action?
    do {
        action = when (Display.awaitLine()) {
            "" -> null
            "quit", "exit", "done" -> Main.Action.Quit()
            else -> Main.Action.Refresh
        }
    } while (action == null)
    return action
}
