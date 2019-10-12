package com.rubyhuntersky.seismic_stampede

import com.rubyhuntersky.seismic_stampede.preinteraction.core.Storybook
import com.rubyhuntersky.seismic_stampede.plots.BuildSessionPlot
import com.rubyhuntersky.seismic_stampede.plots.MainPlot
import com.rubyhuntersky.seismic_stampede.projectors.BuildSessionProjector
import com.rubyhuntersky.seismic_stampede.projectors.MainProjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    val storyBook = Storybook(
        projectors = mapOf(BuildSessionPlot.storyName to BuildSessionProjector)
    )
    MainPlot.start(storyBook).tellBlocking(MainProjector::render)
}

