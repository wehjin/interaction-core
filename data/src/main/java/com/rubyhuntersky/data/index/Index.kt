package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.cash.sum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Index(val constituents: List<Constituent>, val memo: String) {

    @Transient
    val includedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved }
    }

    @Transient
    val includedOrOwnedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved || it.ownedShares > ShareCount.ZERO }
    }

    @Transient
    val totalMarketWeightOfIncludedConstituents: MarketWeight by lazy {
        includedConstituents.map { it.marketWeight }.fold(MarketWeight.ZERO, MarketWeight::plus)
    }

    @Transient
    val cashEquivalentOfAllConstituents: CashEquivalent by lazy {
        if (constituents.isEmpty()) {
            CashEquivalent.ZERO
        } else {
            constituents.map { it.cashEquivalent }.fold(CashEquivalent.ZERO, ::sum)
        }
    }

    fun updateConstituent(constituent: Constituent): Index = Index(constituents.updateConstituent(constituent), memo)

    fun addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Index =
        Index(constituents = constituents.addConstituent(assetSymbol, marketWeight), memo = memo)

    private fun List<Constituent>.addConstituent(
        assetSymbol: AssetSymbol, marketWeight: MarketWeight
    ): List<Constituent> {

        val existing = find { it.assetSymbol == assetSymbol }
        return existing?.let {
            if (marketWeight == it.marketWeight) {
                constituents
            } else {
                updateConstituent(existing.reactivate(marketWeight))
            }
        } ?: updateConstituent(Constituent(assetSymbol, marketWeight))
    }

    private fun List<Constituent>.updateConstituent(constituent: Constituent): List<Constituent> {
        val mutable = toMutableList()
        find { it.assetSymbol == constituent.assetSymbol }?.let { mutable.remove(it) }
        mutable.add(constituent)
        return mutable
    }

    companion object {
        val EMPTY = Index(emptyList(), "")
    }
}
