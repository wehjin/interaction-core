package com.rubyhuntersky.seismic_stampede


object Display : Monitor by IndentMonitor(0) {

    fun awaitLine(): String {
        showPhrase("Seismic:command$  ")
        return readLine()?.trim()!!
    }

    fun showList(label: String, lines: List<String>) {
        if (lines.isEmpty()) {
            showLine("$label: No $label")
        } else {
            showLine("$label:")
            showLine()
            indent {
                it.showLine(lines)
            }
            showLine()
        }
    }

    fun showMap(map: Map<String, String>) {
        map.entries.forEach { (key, value) -> showLine("$key: $value") }
    }

    fun indent(block: (monitor: Monitor) -> Unit) {
        block(IndentMonitor(2, this))
    }

    private object SystemMonitor : Monitor {
        override fun showPhrase(phrases: List<String>) = phrases.forEach(::print)
        override fun showLine(lines: List<String>) = lines.forEach(::println)
    }

    private class IndentMonitor(
        indent: Int,
        private val monitor: Monitor = SystemMonitor
    ) : Monitor {
        private val indentation = (0 until indent).joinToString(separator = "") { " " }
        private var isStartingLine = true

        override fun showPhrase(phrases: List<String>) {
            if (isStartingLine) {
                monitor.showPhrase(indentation)
            }
            monitor.showPhrase(phrases)
            isStartingLine = false
        }

        override fun showLine(lines: List<String>) {
            lines.forEach { line ->
                if (isStartingLine) {
                    monitor.showPhrase(indentation)
                }
                monitor.showLine(line)
            }
            isStartingLine = true
        }
    }
}