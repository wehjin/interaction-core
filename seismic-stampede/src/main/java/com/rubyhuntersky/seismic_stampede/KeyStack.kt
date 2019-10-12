package com.rubyhuntersky.seismic_stampede

sealed class KeyStack {
    object Empty : KeyStack()
    class Shallow(val password: CharArray) : KeyStack()
}