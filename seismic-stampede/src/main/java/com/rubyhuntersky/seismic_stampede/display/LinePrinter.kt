package com.rubyhuntersky.seismic_stampede.display

interface LinePrinter {
    fun printPhrase(phrases: List<String>)
    fun printLine(lines: List<String> = listOf(""))
}

fun LinePrinter.printPhrase(vararg phrases: String) = printPhrase(phrases.asList())
fun LinePrinter.printLine(vararg lines: String) = printLine(lines.asList())
