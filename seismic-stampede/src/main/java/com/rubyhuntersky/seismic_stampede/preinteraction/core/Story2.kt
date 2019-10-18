package com.rubyhuntersky.seismic_stampede.preinteraction.core

import com.rubyhuntersky.seismic_stampede.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

interface Story2<V : Any, A : Any> {
    val family: String
    suspend fun tell(render: (vision: V, offer: (A) -> Boolean) -> RenderStatus)
    fun offer(action: A)
    fun dispose()
}

fun <V, A> Story2<V, A>.tellBlocking(
    render: (vision: V, offerAction: (A) -> Boolean) -> RenderStatus
) where V : Any, A : Any {
    runBlocking { tell(render) }
}

fun <V, A, OutT> Story2<V, A>.toEnding(
    mapToEnd: (V) -> End<OutT>?
): StoryEnding<V, A, OutT> where V : Any, A : Any, OutT : Any {
    val story = this
    return object : StoryEnding<V, A, OutT> {
        override val story: Story2<V, A> = story
        override val endOfStory: (progress: V) -> End<OutT>? = mapToEnd
    }
}

interface StoryEnding<V : Any, A : Any, OutT : Any> {
    val story: Story2<V, A>
    val endOfStory: (vision: V) -> End<OutT>?

}

fun <V, A, OutT, OutA> StoryEnding<V, A, OutT>.toWish(transform: (End<OutT>) -> OutA)
        : Wish2<OutA> where V : Any, A : Any, OutT : Any, OutA : Any {
    return object : Wish2<OutA> {
        override val name = "${story.family}Wish"
        override fun follow(offer: (OutA) -> Boolean): Job {
            return this@toWish.follow { end ->
                offer(transform(end))
            }
        }
    }
}

fun <V, A, R> StoryEnding<V, A, R>.follow(feed: (End<R>) -> Boolean)
        : Job where V : Any, A : Any, R : Any {
    return GlobalScope.launch {
        story.tell { vision, _ ->
            when (val end = endOfStory(vision)) {
                null -> RenderStatus.Repeat
                else -> RenderStatus.Stop.also { feed(end) }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun <V, A> storyOf(
    family: String,
    init: V,
    block: (action: A, vision: V) -> Revision<V, A>
): Story2<V, A> where A : Any, V : Any {
    return object : Story2<V, A> {

        private val actions = Channel<A>(5)
        private val visions = ConflatedBroadcastChannel(init)

        private val job = GlobalScope.launch {
            var vision = init
            val wishJobs = mutableMapOf<String, Job>()
            for (action in actions) {
                val (newVision, isLast, wishes) = block(action, vision)
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
