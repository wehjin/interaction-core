package com.rubyhuntersky.seismic_stampede

import java.io.File

class Vault(folder: File) {

    val depth: Depth = Depth.Public

    val activeGems: List<Gem>
        get() = emptyList()
}