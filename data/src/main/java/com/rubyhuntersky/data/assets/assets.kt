package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashEquivalent


fun product(sharePrice: SharePrice, shareCount: ShareCount): CashEquivalent =
    if (shareCount == ShareCount.ZERO) {
        CashEquivalent.ZERO
    } else {
        when (sharePrice) {
            is SharePrice.Unknown -> CashEquivalent.Unknown()
            is SharePrice.Sample -> CashEquivalent.Amount(sharePrice.cashAmount * shareCount.value)
        }
    }
