package com.rubyhuntersky.seismic_stampede


object PageMonitor : Monitor by IndentMonitor(0) {

    fun awaitCommand(): String? {
        showPhrase(">>>  ")
        return readLine()
    }

    fun showList(label: String, lines: List<String>) {
        showLine("$label:")
        showLine()
        indent {
            if (lines.isEmpty()) {
                it.showLine("No $label")
            } else {
                it.showLine(lines)
            }
        }
        showLine()
    }

    fun showMap(map: Map<String, String>) {
        map.entries.forEach { (key, value) -> showLine("$key: $value") }
    }

    fun indent(block: (monitor: Monitor) -> Unit) {
        block(IndentMonitor(2, this))
    }

    fun startPage() {
        showLine("", "")
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