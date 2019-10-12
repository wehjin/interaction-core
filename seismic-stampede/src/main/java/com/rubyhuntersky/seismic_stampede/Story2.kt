package com.rubyhuntersky.seismic_stampede

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

interface Story2<V : Any, A : Any> {
    val name: String
    fun tellBlocking(render: (vision: V, offerAction: (A) -> Boolean) -> NextStep)
    fun offer(action: A)
    fun dispose()
}

enum class NextStep {
    Repeat,
    Stop
}

@ExperimentalCoroutinesApi
fun <V : Any, A : Any> storyOf(
    name: String,
    init: V,
    block: suspend CoroutineScope.(action: A, vision: V) -> Pair<V, NextStep>
): Story2<V, A> = object : Story2<V, A> {

    private val actions = Channel<A>(5)

    private val visions = ConflatedBroadcastChannel(init)

    private val job = GlobalScope.launch {
        var vision = init
        for (action in actions) {
            val (newVision, stepResult) = block(action, vision)
            if (newVision != vision) {
                vision = newVision.also { visions.send(newVision) }
            }
            if (stepResult == NextStep.Stop) {
                actions.cancel()
            }
        }
        Log.info("$name ended")
    }

    override val name: String
        get() = name

    override fun tellBlocking(render: (vision: V, offerAction: (A) -> Boolean) -> NextStep) {
        runBlocking {
            val visions = visions.openSubscription()
            loop@ for (vision in visions) {
                when (render(vision, actions::offer)) {
                    NextStep.Repeat -> Unit
                    NextStep.Stop -> break@loop
                }
            }
        }
    }

    override fun offer(action: A) {
        GlobalScope.launch {
            actions.offer(action)
        }
    }

    override fun dispose() = job.cancel()
}
