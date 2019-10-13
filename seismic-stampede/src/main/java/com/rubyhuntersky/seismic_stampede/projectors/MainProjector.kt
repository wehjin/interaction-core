package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.KeyStack
import com.rubyhuntersky.seismic_stampede.Log
import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.plots.MainPlot.Action
import com.rubyhuntersky.seismic_stampede.plots.MainPlot.Vision
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object MainProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
        Log.info("VISION: $vision")
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
        Display.printLine()
        Display.printMap(mapOf("Lens" to keyStack.toLens()))
        Display.printList("Gems", vision.session.activeGems.map { it.toString() })
        Display.printLine()
        offer(getViewingAction(vision))
        return RenderStatus.Repeat
    }

    private fun KeyStack.toLens(): String = when (this) {
        is KeyStack.Empty -> "Public"
        is KeyStack.Shallow -> "Protected"
    }

    private fun getViewingAction(viewing: Vision.Viewing): Action {

        var action: Action?
        do {
            val tokens = Tokens(Display.awaitLine())
            action = when (tokens.symbol()) {
                "" -> null
                "add" -> when (val kind = tokens.symbol()) {
                    "note" -> {
                        when (val text = tokens.remaining()) {
                            "" -> null.also { Display.printLine("Usage: add note <text>") }
                            else -> Action.AddNote(text, viewing.session)
                        }
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