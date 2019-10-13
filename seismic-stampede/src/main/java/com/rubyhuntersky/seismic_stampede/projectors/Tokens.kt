package com.rubyhuntersky.seismic_stampede.projectors

class Tokens(private val line: String) {
    private val matchTokenStart = "\\S+".toRegex()
    private var start = 0

    fun symbol(): String {
        val match = matchTokenStart.find(line, start)
        return match?.value?.also { start = match.range.last + 1 } ?: ""
    }

    fun remaining(): String = line.substring(start).trim()
}