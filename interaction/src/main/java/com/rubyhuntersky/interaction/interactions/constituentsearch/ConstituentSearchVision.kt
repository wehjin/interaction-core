package com.rubyhuntersky.interaction.interactions.constituentsearch

sealed class ConstituentSearchVision {
    object Idle : ConstituentSearchVision()
    data class Searching(val searchTerm: String, val results: List<ConstituentSearchResult>) : ConstituentSearchVision()
    object Done : ConstituentSearchVision()
}