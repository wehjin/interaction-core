package com.rubyhuntersky.seismic_stampede.preinteraction.core

import com.rubyhuntersky.seismic_stampede.Log
import com.rubyhuntersky.seismic_stampede.vibes.Wish2
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

interface Story2<V : Any, A : Any> {
    val family: String
    suspend fun tell(render: (vision: V, offer: (A) -> Boolean) -> RenderStatus)
    fun offer(action: A)
    fun dispose()
}

fun <V : Any, A : Any> Story2<V, A>.tellBlocking(
    render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus
) = runBlocking { tell(render) }

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

interface StoryEnding<V : Any, A : Any, R : Any> {
    val story: Story2<V, A>
    val endFromVision: (progress: V) -> End<R>?
}

fun <V : Any, A : Any, R : Any, OutA : Any> StoryEnding<V, A, R>.toWish(transform: (End<R>) -> OutA): Wish2<OutA> {
    return object : Wish2<OutA> {
        override val name = "${story.family}Wish"
        override fun follow(offer: (OutA) -> Boolean) = follow2 { end -> offer(transform(end)) }
    }
}

fun <V : Any, A : Any, R : Any> StoryEnding<V, A, R>.follow2(feed: (End<R>) -> Boolean): Job {
    return GlobalScope.launch {
        story.tell { vision, _ ->
            when (val end = endFromVision(vision)) {
                null -> RenderStatus.Repeat
                else -> RenderStatus.Stop.also { feed(end) }
            }
        }
    }
}

fun <V : Any, A : Any, R : Any> Story2<V, A>.toEnding(mapToEnd: (V) -> End<R>?): StoryEnding<V, A, R> {
    val story = this
    return object : StoryEnding<V, A, R> {
        override val story: Story2<V, A> = story
        override val endFromVision: (progress: V) -> End<R>? = mapToEnd
    }
}

@ExperimentalCoroutinesApi
fun <V : Any, A : Any> storyOf(
    family: String,
    init: V,
    block: (action: A, vision: V) -> Revision<V, A>
): Story2<V, A> {
    return storyOf(family, init, { action, vision, _ -> block(action, vision) })
}

@ExperimentalCoroutinesApi
fun <V : Any, A : Any> storyOf(
    family: String,
    init: V,
    block: (action: A, vision: V, offer: (A) -> Boolean) -> Revision<V, A>
): Story2<V, A> {
    return object : Story2<V, A> {

        private val actions = Channel<A>(5)
        private val visions = ConflatedBroadcastChannel(init)

        private val job = GlobalScope.launch {
            var vision = init
            val wishJobs = mutableMapOf<String, Job>()
            for (action in actions) {
                val (newVision, isLast, wishes) = block(action, vision, actions::offer)
                if (newVision != vision) {
                    vision = newVision.also { visions.send(newVision) }
                }
                wishes.forEach { wish ->
                    wishJobs.remove(wish.name)?.cancel()
                    wish.follow {
                        wishJobs.remove(wish.name)
                        actions.offer(it)
                    }.also {
                        wishJobs[wish.name] = it
                    }
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

        override fun offer(action: A) {
            GlobalScope.launch {
                actions.offer(action)
            }
        }

        override fun dispose() = job.cancel()
    }
}
