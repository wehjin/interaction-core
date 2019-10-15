package com.rubyhuntersky.seismic_stampede

import com.rubyhuntersky.seismic_stampede.plots.GatherPlot
import com.rubyhuntersky.seismic_stampede.plots.MainPlot
import com.rubyhuntersky.seismic_stampede.plots.PasswordPlot
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Storybook
import com.rubyhuntersky.seismic_stampede.projectors.GatherProjector
import com.rubyhuntersky.seismic_stampede.projectors.MainProjector
import com.rubyhuntersky.seismic_stampede.projectors.PasswordProjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    val storyBook = Storybook(
        projectors = mapOf(
            PasswordPlot.storyName to PasswordProjector,
            GatherPlot.storyName to GatherProjector
        )
    )
    MainPlot.start(storyBook).tellBlocking(MainProjector::render)
}

