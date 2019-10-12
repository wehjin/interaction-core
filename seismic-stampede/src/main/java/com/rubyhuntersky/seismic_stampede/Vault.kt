package com.rubyhuntersky.seismic_stampede

import java.io.File

fun loadVault(folder: File): Vault? {
    val passHashFile = passHashFile(folder)
    return if (passHashFile.exists()) Vault(folder) else null
}

fun initVault(folder: File): Vault = Vault(folder)

private fun passHashFile(folder: File) = File(folder, "PASSHASH.BIN")

class Vault(folder: File) {

    val depth: Depth = Depth.Public
    val activeGems: List<Gem>
        get() = emptyList()

}
