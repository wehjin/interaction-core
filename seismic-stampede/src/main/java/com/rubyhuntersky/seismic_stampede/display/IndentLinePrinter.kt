package com.rubyhuntersky.seismic_stampede.display

internal class IndentLinePrinter(
    indent: Int,
    private val linePrinter: LinePrinter = SystemLinePrinter
) : LinePrinter {
    private val indentation = (0 until indent).joinToString(separator = "") { " " }
    private var isStartingLine = true

    override fun printPhrase(phrases: List<String>) {
        if (isStartingLine) {
            linePrinter.printPhrase(indentation)
        }
        linePrinter.printPhrase(phrases)
        isStartingLine = false
    }

    override fun printLine(lines: List<String>) {
        lines.forEach { line ->
            if (isStartingLine) {
                linePrinter.printPhrase(indentation)
            }
            linePrinter.printLine(line)
        }
        isStartingLine = true
    }
}