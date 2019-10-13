package com.rubyhuntersky.seismic_stampede

import java.io.File

fun vaultOf(folder: File) = Vault(folder)

class Vault(folder: File) {

    val depth: Depth = Depth.Public

    val activeGems: List<Gem>
        get() = emptyList()
}
