package com.rubyhuntersky.interaction.core

sealed class InteractionSearch {
    data class ByName(val name: String) : InteractionSearch()
    data class ByKey(val key: Long) : InteractionSearch()
}