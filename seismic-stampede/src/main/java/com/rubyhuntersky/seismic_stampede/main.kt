package com.rubyhuntersky.seismic_stampede

import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")
val monitor = PageMonitor

sealed class KeyStack {
    object Empty : KeyStack()
}

data class Session(
    val keyStack: KeyStack,
    val vault: Vault,
    val refreshCount: Int = 0
) {
    fun refresh() = copy(refreshCount = refreshCount + 1)
}

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

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    val activeFolder = defaultFolder.also { Log.info("Active folder: $it") }
    val story = mainStory(activeFolder)
    story.tellBlocking { vision ->
        when (vision) {
            is Vision.Viewing -> {
                val vault = vision.session.vault
                monitor.showLine()
                monitor.showMap(mapOf("Depth" to vault.depth.name))
                monitor.showList("Gems", vault.activeGems.map { it.toString() })
                monitor.showLine()
                story.offer(getAction())
                LoopAction.Repeat
            }
            is Vision.Ended -> LoopAction.Stop
        }
    }
}

@ExperimentalCoroutinesApi
private fun mainStory(activeFolder: File): Story2<Vision, Action> {
    val init = Vision.Viewing(Session(KeyStack.Empty, Vault(activeFolder))) as Vision
    return storyOf("Main", init) { action, vision ->
        when (action) {
            is Action.Ignore -> Pair(vision, LoopAction.Repeat)
            is Action.Refresh -> Pair((vision as Vision.Viewing).refresh(), LoopAction.Repeat)
            is Action.Quit -> Pair(Vision.Ended, LoopAction.Stop)
        }
    }
}

private fun getAction(): Action {
    var action: Action?
    do {
        action = when (monitor.awaitLine()) {
            "" -> null
            "quit", "exit", "done" -> Action.Quit
            else -> Action.Refresh
        }
    } while (action == null)
    return action
}
