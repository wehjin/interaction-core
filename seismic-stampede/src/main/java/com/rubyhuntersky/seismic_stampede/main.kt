package com.rubyhuntersky.seismic_stampede

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    Main.start().tellBlocking(::renderMain)
}

object Main {

    sealed class Vision {
        data class Viewing(val session: Session) : Vision() {
            fun refresh() = copy(session = session.refresh())
        }

        object Ended : Vision()
    }

    sealed class Action {
        object Ignore : Action()
        object Quit : Action()
        object Refresh : Action()
    }

    private val activeFolder = defaultFolder.also { Log.info("Active folder: $it") }

    @ExperimentalCoroutinesApi
    fun start(): Story2<Vision, Action> {
        val init = Vision.Viewing(Session(KeyStack.Empty, Vault(activeFolder))) as Vision
        return storyOf("Main", init) { action, vision ->
            when (action) {
                is Action.Ignore -> Pair(vision, NextStep.Repeat)
                is Action.Refresh -> Pair(
                    (vision as Vision.Viewing).refresh(),
                    NextStep.Repeat
                )
                is Action.Quit -> Pair(Vision.Ended, NextStep.Stop)
            }
        }
    }
}


private fun renderMain(
    vision: Main.Vision,
    offerAction: (action: Main.Action) -> Boolean
): NextStep {
    return when (vision) {
        is Main.Vision.Viewing -> {
            val vault = vision.session.vault
            Display.showLine()
            Display.showMap(mapOf("Depth" to vault.depth.name))
            Display.showList("Gems", vault.activeGems.map { it.toString() })
            Display.showLine()
            offerAction(getAction())
            NextStep.Repeat
        }
        is Main.Vision.Ended -> NextStep.Stop
    }
}

private fun getAction(): Main.Action {
    var action: Main.Action?
    do {
        action = when (Display.awaitLine()) {
            "" -> null
            "quit", "exit", "done" -> Main.Action.Quit
            else -> Main.Action.Refresh
        }
    } while (action == null)
    return action
}
