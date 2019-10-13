package com.rubyhuntersky.seismic_stampede.preinteraction.core

import com.rubyhuntersky.seismic_stampede.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

interface Story2<V : Any, A : Any> {
    val family: String
    fun tellBlocking(render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus)
    suspend fun tell(render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus)
    fun offer(action: A)
    fun dispose()
}

fun <V : Any, A : Any, OutA : Any> Story2<V, A>.follow(
    feed: (OutA) -> Boolean,
    block: (progress: V) -> OutA?
): Job {
    val story = this
    return GlobalScope.launch {
        story.tell { vision, _ ->
            val outAction = block(vision)
            if (outAction == null) {
                RenderStatus.Repeat
            } else {
                RenderStatus.Stop.also { feed(outAction) }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun <V : Any, A : Any> storyOf(
    family: String,
    init: V,
    block: (action: A, vision: V) -> Revision<V>
): Story2<V, A> {
    return storyOf(family, init, { action, vision, _ -> block(action, vision) })
}

@ExperimentalCoroutinesApi
fun <V : Any, A : Any> storyOf(
    family: String,
    init: V,
    block: (action: A, vision: V, offer: (A) -> Boolean) -> Revision<V>
): Story2<V, A> {
    return object : Story2<V, A> {

        private val actions = Channel<A>(5)
        private val visions = ConflatedBroadcastChannel(init)

        private val job = GlobalScope.launch {
            var vision = init
            for (action in actions) {
                val (newVision, isLast) = block(action, vision, actions::offer)
                if (newVision != vision) {
                    vision = newVision.also { visions.send(newVision) }
                }
                if (isLast) {
                    actions.cancel()
                }
            }
            Log.info("$family ended")
        }

        override val family: String
            get() = family

        override suspend fun tell(render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus) {
            val visions = visions.openSubscription()
            loop@ for (vision in visions) {
                when (render(vision, actions::offer)) {
                    RenderStatus.Repeat -> Unit
                    RenderStatus.Stop -> break@loop
                }
            }
        }

        override fun tellBlocking(render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus) {
            runBlocking { tell(render) }
        }

        override fun offer(action: A) {
            GlobalScope.launch {
                actions.offer(action)
            }
        }

        override fun dispose() = job.cancel()
    }
}
