package com.rubyhuntersky.data.index

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashEquivalent

data class Constituent(
    val assetSymbol: AssetSymbol,
    val marketWeight: MarketWeight,
    val sharePrice: SharePrice = SharePrice.Unknown,
    val ownedShares: ShareCount = ShareCount(0.0),
    val isRemoved: Boolean = false
) {

    val cashEquivalent: CashEquivalent get() = ownedShares * sharePrice

    fun reactivate(marketWeight: MarketWeight?): Constituent = Constituent(
        assetSymbol = assetSymbol,
        marketWeight = marketWeight ?: this.marketWeight,
        sharePrice = sharePrice,
        ownedShares = ownedShares,
        isRemoved = false
    )
}
