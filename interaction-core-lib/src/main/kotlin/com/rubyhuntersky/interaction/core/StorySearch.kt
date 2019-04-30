package com.rubyhuntersky.interaction.core

sealed class StorySearch {
    data class ByName(val name: String) : StorySearch()
    data class ByKey(val key: Long) : StorySearch()
}