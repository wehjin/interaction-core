package com.rubyhuntersky.seismic_stampede

import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")
val monitor = PageMonitor

fun main(args: Array<String>) {
    val activeFolder = defaultFolder.also { Log.info("Active folder: $it") }
    val vault = Vault(activeFolder)
    showVault(vault)
    var repeat = true
    do {
        when (val command = monitor.awaitCommand()?.trim()!!) {
            "quit", "exit", "done" -> {
                monitor.showLine("Done")
                repeat = false
            }
            else -> {
                monitor.showLine(command)
                showVault(vault)
            }
        }
    } while (repeat)
}

private fun showVault(vault: Vault) {
    val gemLines = vault.activeGems.map { it.toString() }
    monitor.startPage()
    monitor.showMap(mapOf("Reading Level" to vault.readingLevel.name))
    monitor.showList("Gems", gemLines)
}
