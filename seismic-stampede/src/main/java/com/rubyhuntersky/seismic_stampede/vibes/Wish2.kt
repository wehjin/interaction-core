package com.rubyhuntersky.seismic_stampede.vibes

import kotlinx.coroutines.Job

interface Wish2<out A : Any> {
    val name: String
    fun follow(offer: (A) -> Boolean): Job
}