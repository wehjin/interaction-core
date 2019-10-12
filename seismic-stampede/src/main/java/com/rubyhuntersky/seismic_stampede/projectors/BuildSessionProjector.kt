package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.plots.BuildSessionPlot
import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.display.printLine

object BuildSessionProjector :
    Projector<BuildSessionPlot.Vision, BuildSessionPlot.Action> {
    override fun render(
        vision: BuildSessionPlot.Vision,
        offer: (BuildSessionPlot.Action) -> Boolean
    ): RenderStatus {
        Display.printLine(vision.toString())
        return RenderStatus.Repeat
    }
}