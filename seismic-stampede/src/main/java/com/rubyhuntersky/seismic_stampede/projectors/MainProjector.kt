package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.KeyStack
import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus
import com.rubyhuntersky.seismic_stampede.stories.MainStory.Action
import com.rubyhuntersky.seismic_stampede.stories.MainStory.Vision

object MainProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
        return when (vision) {
            is Vision.Viewing -> viewingStatus(vision, offer)
            is Vision.Ended -> endedStatus(vision)
        }
    }

    private fun endedStatus(vision: Vision.Ended): RenderStatus {
        vision.message?.let { Display.printLine(it) }
        return RenderStatus.Stop
    }

    private fun viewingStatus(vision: Vision.Viewing, offer: (Action) -> Boolean): RenderStatus {
        val (keyStack, _) = vision.session
        return when (keyStack) {
            is KeyStack.Empty -> RenderStatus.Repeat
            else -> {
                Display.printLine()
                Display.printMap(mapOf("Lens" to keyStack.toLens()))
                Display.printList(
                    label = when (keyStack) {
                        is KeyStack.Empty -> "Notes"
                        is KeyStack.Shallow -> "Secrets"
                    },
                    lines = vision.session.activeGems.map { it.toString() }
                )
                Display.printLine()
                offer(getViewingAction(vision))
                RenderStatus.Repeat
            }
        }
    }

    private fun KeyStack.toLens(): String = when (this) {
        is KeyStack.Empty -> "Public"
        is KeyStack.Shallow -> "Protected"
    }

    private fun getViewingAction(viewing: Vision.Viewing): Action {
        var action: Action?
        do {
            val status = when (viewing.session.keyStack) {
                is KeyStack.Empty -> "PLAIN"
                is KeyStack.Shallow -> "SECRET"
            }
            val tokens = Tokens(Display.awaitLine("Seismic:$status$"))
            action = when (tokens.symbol()) {
                "" -> null
                "add" -> when (val kind = tokens.symbol()) {
                    "note" -> {
                        when (val text = tokens.remaining()) {
                            "" -> null.also { Display.printLine("Usage: add note <text>") }
                            else -> Action.AddNote(text, viewing.session)
                        }
                    }
                    "password" -> {
                        Action.AddPassword(null, null, viewing.session)
                    }
                    else -> null.also { Display.printLine("Token: $kind", "Usage: add [note]") }
                }
                "quit", "exit", "done" -> Action.Quit()
                else -> Action.Refresh
            }
        } while (action == null)
        return action
    }
}