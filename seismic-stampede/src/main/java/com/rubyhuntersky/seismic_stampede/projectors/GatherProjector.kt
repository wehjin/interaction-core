package com.rubyhuntersky.seismic_stampede.projectors

import com.rubyhuntersky.seismic_stampede.display.Display
import com.rubyhuntersky.seismic_stampede.display.printLine
import com.rubyhuntersky.seismic_stampede.stories.GatherStory.Action
import com.rubyhuntersky.seismic_stampede.stories.GatherStory.Vision
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Projector
import com.rubyhuntersky.seismic_stampede.preinteraction.core.RenderStatus

object GatherProjector : Projector<Vision, Action> {
    override fun render(vision: Vision, offer: (Action) -> Boolean) = renderVision(vision, offer)
}

private fun renderVision(vision: Vision, offer: (Action) -> Boolean): RenderStatus {
    return when (vision) {
        is Vision.Gathering -> renderGathering(vision, offer)
        is Vision.Ended -> RenderStatus.Stop
    }
}

private fun renderGathering(vision: Vision.Gathering, offer: (Action) -> Boolean): RenderStatus {
    vision.message?.let { Display.printLine(it) }
    val line = Display.awaitLine("Enter ${vision.activeGather.label}:")
    return RenderStatus.Repeat.also { offer(Action.Advance(line)) }
}
