package com.rubyhuntersky.data

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Index
import com.rubyhuntersky.data.index.MarketWeight

data class Rebellion(val index: Index, val newInvestment: CashAmount) {

    val fullInvestment: CashEquivalent
        get() = index.cashEquivalentOfAllConstituents + newInvestment

    fun addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Rebellion =
        Rebellion(index.addConstituent(assetSymbol, marketWeight), newInvestment)

    companion object {
        val SEED = Rebellion(Index.EMPTY, CashAmount.ZERO)
    }
}
