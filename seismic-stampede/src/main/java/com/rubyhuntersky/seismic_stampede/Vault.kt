package com.rubyhuntersky.seismic_stampede

import java.io.File

fun vaultOf(folder: File) = Vault(folder)

class Vault(folder: File) {

    val activeGems: List<Gem>
        get() = emptyList()
}
