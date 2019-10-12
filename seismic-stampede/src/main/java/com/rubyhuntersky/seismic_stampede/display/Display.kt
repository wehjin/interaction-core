package com.rubyhuntersky.seismic_stampede.display


object Display : LinePrinter by IndentLinePrinter(
    0
) {
    fun awaitLine(): String {
        printPhrase("Seismic:command$  ")
        return readLine()?.trim()!!
    }

    fun printList(label: String, lines: List<String>) {
        if (lines.isEmpty()) {
            printLine("$label: No $label")
        } else {
            printLine("$label:")
            printLine()
            indent { it.printLine(lines) }
            printLine()
        }
    }

    fun printMap(map: Map<String, String>) {
        map.entries.forEach { (key, value) -> printLine("$key: $value") }
    }

    fun indent(block: (lp: LinePrinter) -> Unit) {
        block(IndentLinePrinter(2, this))
    }
}