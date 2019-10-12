package com.rubyhuntersky.seismic_stampede.display

internal object SystemLinePrinter : LinePrinter {
    override fun printPhrase(phrases: List<String>) = phrases.forEach(::print)
    override fun printLine(lines: List<String>) = lines.forEach(::println)
}