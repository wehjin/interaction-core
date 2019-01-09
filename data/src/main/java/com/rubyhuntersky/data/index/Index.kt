package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.assets.AssetSymbol
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

    fun addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Index =
        Index(constituents = constituents.addConstituent(assetSymbol, marketWeight), memo = memo)

    private fun List<Constituent>.addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight):
            List<Constituent> {

        val existing = find { it.assetSymbol == assetSymbol }
        return existing?.let {
            if (marketWeight == it.marketWeight) {
                constituents
            } else {
                toMutableList().apply {
                    remove(existing)
                    add(existing.reactivate(marketWeight))
                }
            }
        } ?: toMutableList().apply { add(Constituent(assetSymbol, marketWeight)) }
    }

    companion object {
        val EMPTY = Index(emptyList(), "")
    }
}
