package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashEquivalent
import kotlinx.serialization.Serializable

@Serializable
data class ShareCount(val value: Double) {
    constructor(long: Long) : this(long.toDouble())

    operator fun times(sharePrice: SharePrice): CashEquivalent = product(sharePrice, this)
    operator fun compareTo(other: ShareCount): Int = value.compareTo(other.value)

    companion object {
        val ZERO = ShareCount(0)
        val ONE = ShareCount(1)
        val TEN = ShareCount(10)
    }
}