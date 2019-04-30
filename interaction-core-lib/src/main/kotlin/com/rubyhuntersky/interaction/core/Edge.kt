package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

open class Edge {
    private var nextKey = 1L
    private val stories = mutableMapOf<Long, Story<*, *, *>>()
    private val disposables = mutableMapOf<Long, Disposable>()

    fun presentStory(story: Story<*, *, *>): Long = addStory(story)

    fun addStory(story: Story<*, *, *>): Long {
        val key = nextKey++
        stories[key] = story
        disposables[key] = story.visions.doOnComplete { removeStory(key) }.subscribe()
        return key
    }

    private fun removeStory(key: Long) {
        stories.remove(key)
        disposables.remove(key)
    }

    fun <V : Any, A : Any, R : Any> findStory(search: StorySearch): Story<V, A, R> {
        val story = when (search) {
            is StorySearch.ByKey ->
                stories[search.key]!!
            is StorySearch.ByName ->
                stories.entries.find { it.value.name == search.name }!!.value
        }
        @Suppress("UNCHECKED_CAST")
        return story as Story<V, A, R>
    }
}