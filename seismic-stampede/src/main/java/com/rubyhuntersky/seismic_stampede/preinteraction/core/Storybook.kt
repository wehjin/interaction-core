package com.rubyhuntersky.seismic_stampede.preinteraction.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Storybook(val projectors: Map<String, Projector<*, *>>) :
    CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()

    fun <V : Any, A : Any> startProjector(story: Story2<V, A>) {
        val renderer = (projectors[story.family] as? Projector<V, A>)
            ?: error("Missing renderer for story ${story.family}")
        launch {
            story.tell(renderer::render)
        }
    }
}