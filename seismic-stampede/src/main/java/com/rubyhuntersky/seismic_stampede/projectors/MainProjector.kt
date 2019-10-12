package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.plots.MainPlot.Action
import com.rubyhuntersky.seismic_stampede.plots.MainPlot.Vision
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object MainProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
        return when (vision) {
            is Vision.Ended -> renderEnded(vision)
            is Vision.Viewing -> renderViewing(vision, offer)
            is Vision.Idle -> RenderStatus.Repeat
        }
    }

    private fun renderEnded(vision: Vision.Ended): RenderStatus {
        vision.message?.let { Display.printLine(it) }
        return RenderStatus.Stop
    }

    private fun renderViewing(vision: Vision.Viewing, offer: (Action) -> Boolean): RenderStatus {
        val vault = vision.session.vault
        Display.printLine()
        Display.printMap(mapOf("Depth" to vault.depth.name))
        Display.printList("Gems", vault.activeGems.map { it.toString() })
        Display.printLine()
        offer(getViewingAction())
        return RenderStatus.Repeat
    }

    private fun getViewingAction(): Action {
        var action: Action?
        do {
            action = when (Display.awaitLine()) {
                "" -> null
                "quit", "exit", "done" -> Action.Quit()
                else -> Action.Refresh
            }
        } while (action == null)
        return action
    }
}