package com.rubyhuntersky.seismic_stampede

interface Monitor {
    fun showPhrase(phrases: List<String>)
    fun showLine(lines: List<String> = listOf(""))
}

fun Monitor.showPhrase(vararg phrases: String) = showPhrase(phrases.asList())
fun Monitor.showLine(vararg lines: String) = showLine(lines.asList())
