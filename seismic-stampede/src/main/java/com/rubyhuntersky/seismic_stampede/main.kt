package com.rubyhuntersky.seismic_stampede

import java.io.File

val defaultFolder = File(System.getenv("HOME"), ".seismic-stampede")

fun main(args: Array<String>) {
    val activeFolder = defaultFolder.also { Log.info("Active folder: $it") }
    val vault = Vault(activeFolder)
}