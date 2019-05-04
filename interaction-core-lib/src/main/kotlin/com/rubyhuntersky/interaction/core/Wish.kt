package com.rubyhuntersky.interaction.core

import io.reactivex.Single

data class Wish<A>(
    val action: Single<A>,
    val name: String? = null
)