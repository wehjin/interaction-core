package com.rubyhuntersky.seismic_stampede

import com.rubyhuntersky.seismic_stampede.stories.GatherStory
import com.rubyhuntersky.seismic_stampede.stories.MainStory
import com.rubyhuntersky.seismic_stampede.stories.PasswordStory
import com.rubyhuntersky.seismic_stampede.preinteraction.core.Storybook
import com.rubyhuntersky.seismic_stampede.preinteraction.core.tellBlocking
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
            PasswordStory.storyName to PasswordProjector,
            GatherStory.storyName to GatherProjector
        )
    )
    MainStory.start(storyBook).tellBlocking(MainProjector::render)
}

