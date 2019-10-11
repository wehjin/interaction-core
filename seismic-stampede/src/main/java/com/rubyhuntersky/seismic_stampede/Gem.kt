package com.rubyhuntersky.seismic_stampede

sealed class Gem {
    data class Note(val title: String, val text: String) : Gem()
}