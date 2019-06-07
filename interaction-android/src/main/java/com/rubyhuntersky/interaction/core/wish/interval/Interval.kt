package com.rubyhuntersky.interaction.core.wish.interval

import java.util.concurrent.TimeUnit

data class Interval(
    val period: Long,
    val timeUnit: TimeUnit
)