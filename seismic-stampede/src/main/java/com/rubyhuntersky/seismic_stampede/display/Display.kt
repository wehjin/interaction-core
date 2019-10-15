package com.rubyhuntersky.seismic_stampede.display


object Display : LinePrinter by IndentLinePrinter(
    0
) {
    fun awaitLine(prompt: String = "Seismic:command$"): String {
        printPhrase("$prompt  ")
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

    fun awaitPassword(prompt: String = "Password"): CharArray? {
        return System.console()?.readPassword("$prompt: ")
            ?: Unit.let {
                print("$prompt (No console -password will be visible): ")
                val source = System.`in`
                val bytes = ByteArray(50)
                val count = source.read(bytes) - 1
                if (count > 0) {
                    (0 until count).fold(
                        initial = CharArray(count),
                        operation = { chars, index ->
                            chars.also {
                                it[index] = bytes[index].toChar()
                            }
                        }
                    ).also {
                        (0 until count).forEach { bytes[it] = 0 }
                    }
                } else {
                    null
                }
            }
    }
}