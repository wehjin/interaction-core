package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.cash.sum

data class Index(val constituents: List<Constituent>, val memo: String) {

    val includedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved }
    }

    val includedOrOwnedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved || it.ownedShares > ShareCount.ZERO }
    }

    val totalMarketWeightOfIncludedConstituents: MarketWeight by lazy {
        includedConstituents.map { it.marketWeight }.fold(MarketWeight.ZERO, MarketWeight::plus)
    }

    val cashEquivalentOfAllConstituents: CashEquivalent by lazy {
        if (constituents.isEmpty()) {
            CashEquivalent.ZERO
        } else {
            constituents.map { it.cashEquivalent }.fold(CashEquivalent.ZERO, ::sum)
        }
    }

    companion object {
        val EMPTY = Index(emptyList(), "")
    }
}
