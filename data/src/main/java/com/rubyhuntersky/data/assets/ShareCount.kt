package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashEquivalent

data class ShareCount(private val count: Double) {

    operator fun times(price: SharePrice): CashEquivalent = when (price) {
        SharePrice.Unknown -> CashEquivalent.Unknown
        is SharePrice.Sampled -> CashEquivalent.Amount(price.cashAmount * count)
    }

    companion object {

        val ONE = ShareCount(1.0)
    }
}