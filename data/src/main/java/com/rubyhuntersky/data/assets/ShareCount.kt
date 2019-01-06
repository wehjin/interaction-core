package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashEquivalent

data class ShareCount(private val count: Double) {
    constructor(long: Long) : this(long.toDouble())

    operator fun times(price: SharePrice): CashEquivalent = when (price) {
        SharePrice.UNKNOWN -> CashEquivalent.UNKNOWN
        is SharePrice.SAMPLE -> CashEquivalent.AMOUNT(price.cashAmount * count)
    }

    companion object {

        val ONE = ShareCount(1)
        val TEN = ShareCount(10)
    }
}