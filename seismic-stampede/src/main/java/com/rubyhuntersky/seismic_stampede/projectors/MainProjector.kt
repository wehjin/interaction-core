package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.plots.MainPlot
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object MainProjector :
    Projector<MainPlot.Vision, MainPlot.Action> {
    override fun render(
        vision: MainPlot.Vision,
        offer: (MainPlot.Action) -> Boolean
    ): RenderStatus {
        return when (vision) {
            is MainPlot.Vision.Ended -> {
                vision.message?.let { Display.printLine(it) }
                RenderStatus.Stop
            }
            is MainPlot.Vision.Viewing -> {
                val vault = vision.session.vault
                Display.printLine()
                Display.printMap(mapOf("Depth" to vault.depth.name))
                Display.printList("Gems", vault.activeGems.map { it.toString() })
                Display.printLine()
                offer(getAction())
                RenderStatus.Repeat
            }
            is MainPlot.Vision.Idle -> RenderStatus.Repeat
        }
    }

    private fun getAction(): MainPlot.Action {
        var action: MainPlot.Action?
        do {
            action = when (Display.awaitLine()) {
                "" -> null
                "quit", "exit", "done" -> MainPlot.Action.Quit()
                else -> MainPlot.Action.Refresh
            }
        } while (action == null)
        return action
    }
}