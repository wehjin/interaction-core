package com.rubyhuntersky.seismic_stampede

import java.io.File

class Vault(folder: File) {

    val readingLevel: ReadingLevel = ReadingLevel.Public

    val activeGems: List<Gem>
        get() = emptyList()
}